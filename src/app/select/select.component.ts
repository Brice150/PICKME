import { CommonModule } from '@angular/common';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  Component,
  OnDestroy,
  OnInit,
  inject,
} from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { User } from '../core/interfaces/user';
import { SelectService } from '../core/services/select.service';
import { CardComponent } from './card/card.component';
import { LoadingCardComponent } from './loading-card/loading-card.component';

@Component({
  selector: 'app-select',
  imports: [CommonModule, CardComponent, LoadingCardComponent],
  templateUrl: './select.component.html',
  styleUrl: './select.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SelectComponent implements OnInit, OnDestroy {
  toastr = inject(ToastrService);
  selectService = inject(SelectService);
  router = inject(Router);

  users: User[] = [];
  destroyed$: Subject<void> = new Subject<void>();
  activeMatchAnimation: boolean = false;
  loading: boolean = true;
  isLoading: boolean = false;
  page: number = 0;
  maxLoadedIndex: number = 0;
  initLoading: boolean = true;

  ngOnInit(): void {
    this.initLoading = true;
    this.loadUsers();
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  loadUsers(): void {
    this.loading = true;
    this.selectService
      .getAllSelectedUsers(this.page)
      .pipe(takeUntil(this.destroyed$))
      .subscribe({
        next: (users: User[]) => {
          this.users.push(...users);
          this.initLoading = false;
          this.loading = false;
          setTimeout(() => {
            document.querySelector('swiper-container')?.swiper.update();
          });
        },
      });
  }

  isCurrentView(user: User): boolean {
    const index: number | undefined =
      document.querySelector('swiper-container')?.swiper.activeIndex;
    if (index === undefined) {
      return false;
    }
    return user.id === this.users[index]?.id;
  }

  onSlideChange(): void {
    const index: number | undefined =
      document.querySelector('swiper-container')?.swiper.activeIndex;
    if (index !== undefined) {
      const shouldLoadNextPage =
        index % 15 === 0 && index > this.maxLoadedIndex;
      if (shouldLoadNextPage) {
        this.maxLoadedIndex = index;
        this.page++;
        this.loadUsers();
      }
    }
  }

  like(user: User): void {
    this.isLoading = true;
    this.selectService.addLike(user.id!).subscribe({
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
            }
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
            }
          );
        }
      },
    });
  }

  dislike(user: User): void {
    this.isLoading = true;
    this.selectService.addDislike(user.id!).subscribe({
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
          }
        );
      },
    });
  }

  removeSlide(userId: number): void {
    const userIndex = this.users.findIndex((user: User) => user.id === userId);
    if (userIndex !== -1) {
      document.querySelector('swiper-container')?.swiper.removeSlide(userIndex);
      this.users.splice(userIndex, 1);
    }
  }

  goTo(action: string): void {
    if (action === 'profile') {
      this.router.navigate(['/profile']);
    } else {
      document.querySelector('swiper-container')?.swiper.slideTo(0);
    }
  }
}
