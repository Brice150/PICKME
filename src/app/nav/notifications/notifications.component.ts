import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NotificationComponent } from './notification/notification.component';
import { Notification } from '../../core/interfaces/notification';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, NotificationComponent],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css',
})
export class NotificationsComponent {
  @Input() notifications: Notification[] = [];
  @Output() goToEvent: EventEmitter<string> = new EventEmitter<string>();

  goTo(link: string): void {
    this.goToEvent.emit(link);
  }
}
