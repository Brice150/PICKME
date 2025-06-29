import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { Notification } from '../../../core/interfaces/notification';
import { CustomDatePipe } from '../../../shared/pipes/custom-date.pipe';
import { DescriptionPipe } from '../../../shared/pipes/description.pipe';

@Component({
    selector: 'app-notification',
    imports: [CommonModule, CustomDatePipe, DescriptionPipe],
    templateUrl: './notification.component.html',
    styleUrl: './notification.component.css'
})
export class NotificationComponent {
  readonly notification = input.required<Notification>();
}
