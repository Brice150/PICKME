import { CommonModule } from '@angular/common';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { User } from '../core/interfaces/user';
import { CardComponent } from './card/card.component';
import { ToastrService } from 'ngx-toastr';
import { SelectService } from '../core/services/select.service';
import { Subject, takeUntil } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { LoadingComponent } from '../shared/components/loading/loading.component';

@Component({
  selector: 'app-select',
  standalone: true,
  imports: [CommonModule, CardComponent, LoadingComponent],
  templateUrl: './select.component.html',
  styleUrl: './select.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SelectComponent implements OnInit, OnDestroy {
  users: User[] = [];
  destroyed$: Subject<void> = new Subject<void>();
  activeMatchAnimation: boolean = false;
  loading: boolean = true;

  constructor(
    private toastr: ToastrService,
    private selectService: SelectService
  ) {}

  ngOnInit(): void {
    this.selectService
      .getAllSelectedUsers()
      .pipe(takeUntil(this.destroyed$))
      .subscribe({
        next: (users: User[]) => {
          this.users = users;
          this.loading = false;
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Error', {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          });
        },
      });
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  isCurrentView(user: User): boolean {
    const index: number | undefined =
      document.querySelector('swiper-container')?.swiper.activeIndex;
    if (index === undefined) {
      return false;
    }
    return user.id === this.users[index].id;
  }

  onSlideChange(): void {
    // Needed to update isCurrentView when we slide
  }

  like(user: User): void {
    this.selectService.addLike(user.id!).subscribe({
      next: (matchNotification: string) => {
        if (matchNotification && matchNotification !== '') {
          this.activeMatchAnimation = true;
          setTimeout(() => {
            this.activeMatchAnimation = false;
            this.removeSlide(user.id!);
            this.toastr.success(
              'You have a match with ' + matchNotification,
              'Matched ' + matchNotification,
              {
                positionClass: 'toast-bottom-center',
                toastClass: 'ngx-toastr custom gold',
              }
            );
          }, 2000);
        } else {
          this.removeSlide(user.id!);
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
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, 'Error', {
          positionClass: 'toast-bottom-center',
          toastClass: 'ngx-toastr custom',
        });
      },
    });
  }

  dislike(user: User): void {
    this.selectService.addDislike(user.id!).subscribe({
      next: () => {
        this.removeSlide(user.id!);
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, 'Error', {
          positionClass: 'toast-bottom-center',
          toastClass: 'ngx-toastr custom',
        });
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
}
