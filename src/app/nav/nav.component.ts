import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import {
  Subject,
  distinctUntilChanged,
  repeat,
  switchMap,
  takeUntil,
} from 'rxjs';
import { Notification } from '../core/interfaces/notification';
import { ConnectService } from '../core/services/connect.service';
import { NotificationService } from '../core/services/notification.service';
import {
  MenuAnimation,
  NavButtonAnimation,
  NotificationAnimation,
  NotificationLogoAnimation,
} from './nav-animation';
import { NavButtonsComponent } from './nav-buttons/nav-buttons.component';
import { NotificationsComponent } from './notifications/notifications.component';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    NavButtonsComponent,
    NotificationsComponent,
  ],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css',
  animations: [
    MenuAnimation,
    NotificationLogoAnimation,
    NavButtonAnimation,
    NotificationAnimation,
  ],
})
export class NavComponent implements OnInit {
  isMenuActive: boolean = false;
  isNotificationsActive: boolean = false;
  notifications: Notification[] = [];
  destroyed$: Subject<void> = new Subject<void>();

  constructor(
    public connectService: ConnectService,
    private notificationService: NotificationService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.connectService.connectedUserReady$
      .pipe(
        switchMap(() => {
          return this.notificationService
            .getAllUserNotifications()
            .pipe(
              repeat({ delay: 10000 }),
              distinctUntilChanged(),
              takeUntil(this.connectService.loggedOut$)
            );
        })
      )
      .subscribe((notifications: Notification[]) => {
        if (
          this.router.url === '/match' &&
          notifications[0].link !== 'unmatch' &&
          notifications[0].link !== 'delete'
        ) {
          this.setAllNotificationsToSeenWithNewNotifications(notifications);
        } else {
          this.notifications = notifications;
        }
      });
  }

  toggleMenu(): void {
    this.isMenuActive = !this.isMenuActive;
    if (this.isNotificationsActive) {
      this.setAllNotificationsToSeen();
    }
    this.isNotificationsActive = false;
  }

  toggleNotifications(): void {
    if (this.isNotificationsActive) {
      this.setAllNotificationsToSeen();
    }
    this.isNotificationsActive = !this.isNotificationsActive;
  }

  setAllNotificationsToSeen(): void {
    if (
      this.notifications.some(
        (notification: Notification) => !notification.seen
      )
    ) {
      this.notificationService.markUserNotificationsAsSeen().subscribe({
        next: () => {
          this.notifications.forEach(
            (notification: Notification) => (notification.seen = true)
          );
        },
      });
    }
  }

  setAllNotificationsToSeenWithNewNotifications(
    notifications: Notification[]
  ): void {
    if (
      notifications.some((notification: Notification) => !notification.seen)
    ) {
      this.notificationService.markUserNotificationsAsSeen().subscribe({
        next: () => {
          notifications.forEach(
            (notification: Notification) => (notification.seen = true)
          );
          this.notifications = notifications;
        },
      });
    }
  }

  getUnseenNotificationsLenght(): number {
    return this.notifications.filter(
      (notification: Notification) => !notification.seen
    ).length;
  }

  goTo(): void {
    this.toggleMenu();
    this.router.navigate(['match']);
  }

  logout(): void {
    this.connectService.logout();
    this.toggleMenu();
    this.toastr.success('You are logged out', 'Logged Out', {
      positionClass: 'toast-bottom-center',
      toastClass: 'ngx-toastr custom',
    });
  }
}
