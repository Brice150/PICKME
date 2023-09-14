import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { User } from '../../core/interfaces/user';
import { UserService } from '../../core/services/user.service';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css'],
})
export class NavComponent implements OnInit, OnDestroy {
  loggedInUserEmail!: string | null;
  isAdmin!: boolean;
  getUserRoleSubscription!: Subscription;

  constructor(
    private router: Router,
    private userService: UserService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.getUserRole();
  }

  ngOnDestroy() {
    this.getUserRoleSubscription && this.getUserRoleSubscription.unsubscribe();
  }

  getUserRole() {
    this.getUserRoleSubscription = this.userService
      .getConnectedUser()
      .subscribe({
        next: (response: User) => {
          if (response.userRole === 'ROLE_ADMIN') {
            this.isAdmin = true;
          } else {
            this.isAdmin = false;
          }
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Server error', {
            positionClass: 'toast-bottom-center',
          });
        },
      });
  }

  extend() {
    let buttons = document.querySelector('.buttons');
    if (!document.querySelector('.active')) {
      buttons?.classList.add('active');
    } else {
      buttons?.classList.remove('active');
    }
  }

  logout() {
    this.router.navigate(['/connect']).then(() => {
      this.toastr.success('Logged out', 'Connection', {
        positionClass: 'toast-bottom-center',
      });
    });
  }
}
