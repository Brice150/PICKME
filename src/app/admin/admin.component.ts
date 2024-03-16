import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { User } from '../core/interfaces/user';
import { AdminCardComponent } from './admin-card/admin-card.component';
import { AdminService } from '../core/services/admin.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, AdminCardComponent, FormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent {
  isSortingByDislike: boolean = false;
  isSortingByLike: boolean = true;
  isSortingByMatch: boolean = false;
  search!: string;
  users: User[] = this.adminService.users;
  filteredUsers: User[] = [...this.users];

  constructor(
    private toastr: ToastrService,
    private adminService: AdminService
  ) {}

  sortByDislike(): void {
    if (!this.isSortingByDislike) {
      this.isSortingByDislike = true;
      this.isSortingByLike = false;
      this.isSortingByMatch = false;
      this.users.sort(
        (user1: User, user2: User) =>
          user2.totalDislikes! - user1.totalDislikes!
      );
      this.searchByNickname('Sorted By Max Dislikes');
    }
  }

  sortByLike(): void {
    if (!this.isSortingByLike) {
      this.isSortingByDislike = false;
      this.isSortingByLike = true;
      this.isSortingByMatch = false;
      this.users.sort(
        (user1: User, user2: User) => user2.totalLikes! - user1.totalLikes!
      );
      this.searchByNickname('Sorted By Max Likes');
    }
  }

  sortByMatch(): void {
    if (!this.isSortingByMatch) {
      this.isSortingByDislike = false;
      this.isSortingByLike = false;
      this.isSortingByMatch = true;
      this.users.sort(
        (user1: User, user2: User) => user2.totalMatches! - user1.totalMatches!
      );
      this.searchByNickname('Sorted By Max Matches');
    }
  }

  searchByNickname(message: string): void {
    if (!this.search || this.search === '') {
      this.filteredUsers = [...this.users];
    } else {
      this.filteredUsers = [...this.users].filter((user: User) =>
        user.nickname
          .toLocaleLowerCase()
          .includes(this.search.toLocaleLowerCase())
      );
    }
    if (message && message.includes('Sorted By Max')) {
      this.toastr.success('Users have been sorted', message, {
        positionClass: 'toast-bottom-center',
        toastClass: 'ngx-toastr custom',
      });
    } else if (message && message === 'User Deleted') {
      this.toastr.success('User has been deleted', message, {
        positionClass: 'toast-bottom-center',
        toastClass: 'ngx-toastr custom',
      });
    }
  }

  deleteUser(userToDelete: User): void {
    const userIndex = this.users.findIndex(
      (user: User) => user.id === userToDelete.id
    );
    if (userIndex !== -1) {
      this.users.splice(userIndex, 1);
      this.searchByNickname('User Deleted');
    }
  }
}
