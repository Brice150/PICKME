import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { ConnectService } from '../core/services/connect.service';
import { NavAnimation, NotificationAnimation } from './nav-animation';
import { NavButtonsComponent } from './nav-buttons/nav-buttons.component';
import { Notification } from '../core/interfaces/notification';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [CommonModule, RouterModule, NavButtonsComponent],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css',
  animations: [NavAnimation, NotificationAnimation],
})
export class NavComponent implements OnInit {
  isMenuActive: boolean = false;
  notifications: Notification[] = [];

  constructor(
    public connectService: ConnectService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.notifications.push({
      id: 1,
      content: 'first notification',
      link: 'profile',
      seen: false,
    });
  }

  toggleMenu(): void {
    this.isMenuActive = !this.isMenuActive;
  }

  openNotifications(): void {}

  getUnseenNotificationsLenght(): number {
    return this.notifications.filter(
      (notification: Notification) => !notification.seen
    ).length;
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
