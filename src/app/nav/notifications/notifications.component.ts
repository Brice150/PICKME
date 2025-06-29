import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Notification } from '../../core/interfaces/notification';
import { NotificationComponent } from './notification/notification.component';

@Component({
    selector: 'app-notifications',
    imports: [CommonModule, NotificationComponent],
    templateUrl: './notifications.component.html',
    styleUrl: './notifications.component.css'
})
export class NotificationsComponent {
  @Input() notifications: Notification[] = [];
  @Output() goToEvent: EventEmitter<void> = new EventEmitter<void>();

  goTo(): void {
    this.goToEvent.emit();
  }
}
