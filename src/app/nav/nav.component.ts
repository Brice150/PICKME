import { NgClass } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { EMPTY, catchError, startWith, switchMap, takeUntil } from 'rxjs';
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
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgClass, RouterModule, NavButtonsComponent, NotificationsComponent],
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
  readonly connectService = inject(ConnectService);
  private readonly notificationService = inject(NotificationService);
  private readonly toastr = inject(ToastrService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly isMenuActive = signal(false);
  readonly isNotificationsActive = signal(false);
  readonly notifications = signal<Notification[]>([]);
  /** Drives the badge, so it is derived from the list instead of being counted by the template. */
  readonly unseenNotificationsCount = computed(
    () =>
      this.notifications().filter(
        (notification: Notification) => !notification.seen,
      ).length,
  );

  ngOnInit(): void {
    this.connectService.connectedUserReady$
      .pipe(
        switchMap(() =>
          // Read once on connection, then again on every signal the server sends.
          this.notificationService.serverEvents$.pipe(
            startWith(undefined),
            // A failed read costs one refresh, not the whole subscription: the bell keeps
            // following the stream and catches up on the next event.
            switchMap(() =>
              this.notificationService
                .getAllUserNotifications()
                .pipe(catchError(() => EMPTY)),
            ),
            takeUntil(this.connectService.loggedOut$),
          ),
        ),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((notifications: Notification[]) => {
        if (
          this.router.url === '/match' &&
          notifications[0]?.link !== 'unmatch' &&
          notifications[0]?.link !== 'delete'
        ) {
          this.setAllNotificationsToSeenWithNewNotifications(notifications);
        } else {
          this.notifications.set(notifications);
        }
      });
  }

  toggleMenu(): void {
    this.isMenuActive.update((active: boolean) => !active);
    if (this.isNotificationsActive()) {
      this.setAllNotificationsToSeen();
    }
    this.isNotificationsActive.set(false);
  }

  toggleNotifications(): void {
    if (this.isNotificationsActive()) {
      this.setAllNotificationsToSeen();
    }
    this.isNotificationsActive.update((active: boolean) => !active);
  }

  setAllNotificationsToSeen(): void {
    this.markAsSeen(this.notifications());
  }

  setAllNotificationsToSeenWithNewNotifications(
    notifications: Notification[],
  ): void {
    this.markAsSeen(notifications);
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

  /**
   * Marks a batch as read on the server, then publishes it read. The batch is republished instead
   * of being edited in place: the badge is derived from the list, and a signal only reports a
   * value it has been given.
   */
  private markAsSeen(notifications: Notification[]): void {
    if (
      !notifications.some((notification: Notification) => !notification.seen)
    ) {
      return;
    }
    this.notificationService.markUserNotificationsAsSeen().subscribe({
      next: () => {
        this.notifications.set(
          notifications.map((notification: Notification) => ({
            ...notification,
            seen: true,
          })),
        );
      },
    });
  }
}
