import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { Message } from '../../core/interfaces/message';
import { CustomDatePipe } from '../../shared/pipes/custom-date.pipe';

@Component({
    selector: 'app-message',
    imports: [CommonModule, CustomDatePipe],
    templateUrl: './message.component.html',
    styleUrl: './message.component.css'
})
export class MessageComponent {
  readonly message = input.required<Message>();
  readonly userName = input.required<string>();
}
