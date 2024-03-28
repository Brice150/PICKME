import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { ConnectService } from '../core/services/connect.service';
import {
  MenuAnimation,
  NavButtonAnimation,
  NotificationAnimation,
  NotificationLogoAnimation,
} from './nav-animation';
import { NavButtonsComponent } from './nav-buttons/nav-buttons.component';
import { Notification } from '../core/interfaces/notification';
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

  constructor(
    public connectService: ConnectService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  ngOnInit(): void {
    //TODO
    this.isMenuActive = true;
    this.isNotificationsActive = true;
    this.notifications.push({
      id: 1,
      content: 'Welcome, you can start by completing your profile !',
      link: 'profile',
      date: new Date(),
      seen: true,
    });
    this.notifications.push({
      id: 2,
      content: 'New match with Vanessa',
      link: 'match',
      date: new Date(),
      seen: false,
    });
    this.notifications.push({
      id: 3,
      content: 'New message from Vanessa',
      link: 'match',
      date: new Date(),
      seen: false,
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
    this.isNotificationsActive = !this.isNotificationsActive;
    this.setAllNotificationsToSeen();
  }

  setAllNotificationsToSeen(): void {
    this.notifications.forEach(
      (notification: Notification) => (notification.seen = true)
    );
  }

  getUnseenNotificationsLenght(): number {
    return this.notifications.filter(
      (notification: Notification) => !notification.seen
    ).length;
  }

  goTo(link: string): void {
    this.toggleMenu();
    this.router.navigate([link]);
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
