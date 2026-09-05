import type { Swiper } from 'swiper';
import type { SwiperContainer } from 'swiper/element';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  Component,
  DestroyRef,
  ElementRef,
  OnInit,
  inject,
  viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { User } from '../core/interfaces/user';
import { SelectService } from '../core/services/select.service';
import { CardComponent } from './card/card.component';
import { LoadingCardComponent } from './loading-card/loading-card.component';

const SLIDES_BEFORE_NEXT_PAGE = 15;

@Component({
  selector: 'app-select',
  imports: [CardComponent, LoadingCardComponent],
  templateUrl: './select.component.html',
  styleUrl: './select.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SelectComponent implements OnInit {
  private readonly toastr = inject(ToastrService);
  private readonly selectService = inject(SelectService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  users: User[] = [];
  activeMatchAnimation = false;
  loading = true;
  isLoading = false;
  initLoading = true;
  activeIndex = 0;

  private page = 0;
  private maxLoadedIndex = 0;

  ngOnInit(): void {
    this.initLoading = true;
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.selectService
      .getAllSelectedUsers(this.page)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (users: User[]) => {
          this.users.push(...users);
          this.initLoading = false;
          this.loading = false;
          setTimeout(() => {
            this.getSwiper()?.update();
          });
        },
      });
  }

  onSlideChange(): void {
    const index: number | undefined = this.getSwiper()?.activeIndex;
    if (index === undefined) {
      return;
    }
    this.activeIndex = index;
    const shouldLoadNextPage =
      index % SLIDES_BEFORE_NEXT_PAGE === 0 && index > this.maxLoadedIndex;
    if (shouldLoadNextPage) {
      this.maxLoadedIndex = index;
      this.page++;
      this.loadUsers();
    }
  }

  like(user: User): void {
    this.isLoading = true;
    this.selectService
      .addLike(user.id!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (matchNotification: string) => {
          if (matchNotification && matchNotification !== '') {
            this.activeMatchAnimation = true;
            setTimeout(() => {
              this.activeMatchAnimation = false;
              this.removeSlide(user.id!);
              this.isLoading = false;
            }, 3000);
            this.toastr.success(
              'You have a match with ' + matchNotification,
              'Matched ' + matchNotification,
              {
                positionClass: 'toast-bottom-center',
                toastClass: 'ngx-toastr custom gold',
              },
            );
          } else {
            this.removeSlide(user.id!);
            this.isLoading = false;
            this.toastr.success(
              'You have liked ' + user.nickname,
              'Liked ' + user.nickname,
              {
                positionClass: 'toast-bottom-center',
                toastClass: 'ngx-toastr custom',
              },
            );
          }
        },
      });
  }

  dislike(user: User): void {
    this.isLoading = true;
    this.selectService
      .addDislike(user.id!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.removeSlide(user.id!);
          this.isLoading = false;
        },
        complete: () => {
          this.toastr.success(
            'You have disliked ' + user.nickname,
            'Disliked ' + user.nickname,
            {
              positionClass: 'toast-bottom-center',
              toastClass: 'ngx-toastr custom',
            },
          );
        },
      });
  }

  removeSlide(userId: number): void {
    const userIndex = this.users.findIndex((user: User) => user.id === userId);
    if (userIndex !== -1) {
      this.getSwiper()?.removeSlide(userIndex);
      this.users.splice(userIndex, 1);
      this.activeIndex = this.getSwiper()?.activeIndex ?? this.activeIndex;
    }
  }

  goTo(action: string): void {
    if (action === 'profile') {
      this.router.navigate(['/profile']);
    } else {
      this.getSwiper()?.slideTo(0);
    }
  }

  // Resolved from the template rather than from the whole document: a global query would find
  // the carousel of another screen, and the custom element is only upgraded after the first
  // render, which leaves the swiper instance undefined until then.
  private readonly carousel =
    viewChild<ElementRef<SwiperContainer>>('carousel');

  private getSwiper(): Swiper | undefined {
    return this.carousel()?.nativeElement.swiper;
  }
}
