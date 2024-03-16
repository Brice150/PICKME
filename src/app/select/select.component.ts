import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, Component } from '@angular/core';
import { User } from '../core/interfaces/user';
import { CardComponent } from './card/card.component';
import { ToastrService } from 'ngx-toastr';
import { SelectService } from '../core/services/select.service';

@Component({
  selector: 'app-select',
  standalone: true,
  imports: [CommonModule, CardComponent],
  templateUrl: './select.component.html',
  styleUrl: './select.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SelectComponent {
  users: User[] = this.selectService.users;

  constructor(
    private toastr: ToastrService,
    private selectService: SelectService
  ) {}

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

  like(message: string, user: User): void {
    // TODO : likeUser in the backend
    this.removeSlide(user.id!);
    if (message && message === 'match') {
      this.toastr.success(
        'You have a match with ' + user.nickname,
        'Matched ' + user.nickname,
        {
          positionClass: 'toast-bottom-center',
          toastClass: 'ngx-toastr custom gold',
        }
      );
    } else {
      this.toastr.success(
        'You have liked ' + user.nickname,
        'Liked ' + user.nickname,
        {
          positionClass: 'toast-bottom-center',
          toastClass: 'ngx-toastr custom',
        }
      );
    }
  }

  dislike(user: User): void {
    // TODO : dislikeUser in the backend
    this.removeSlide(user.id!);
    this.toastr.success(
      'You have disliked ' + user.nickname,
      'Disliked ' + user.nickname,
      {
        positionClass: 'toast-bottom-center',
        toastClass: 'ngx-toastr custom',
      }
    );
  }

  removeSlide(userId: number): void {
    const userIndex = this.users.findIndex((user: User) => user.id === userId);
    if (userIndex !== -1) {
      document.querySelector('swiper-container')?.swiper.removeSlide(userIndex);
      this.users.splice(userIndex, 1);
    }
  }
}
