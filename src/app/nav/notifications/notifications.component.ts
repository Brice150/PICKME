import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from '@angular/core';
import { Notification } from '../../core/interfaces/notification';
import { NotificationComponent } from './notification/notification.component';

@Component({
  selector: 'app-notifications',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NotificationComponent],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css',
})
export class NotificationsComponent {
  readonly notifications = input<Notification[]>([]);
  readonly goToEvent = output<void>();

  goTo(): void {
    this.goToEvent.emit();
  }
}
