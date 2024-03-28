import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Notification } from '../../../core/interfaces/notification';
import { CustomDatePipe } from '../../../shared/pipes/custom-date.pipe';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule, CustomDatePipe],
  templateUrl: './notification.component.html',
  styleUrl: './notification.component.css',
})
export class NotificationComponent {
  @Input() notification!: Notification;
}
