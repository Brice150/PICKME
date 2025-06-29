import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Message } from '../../core/interfaces/message';
import { CustomDatePipe } from '../../shared/pipes/custom-date.pipe';

@Component({
    selector: 'app-message',
    imports: [CommonModule, CustomDatePipe],
    templateUrl: './message.component.html',
    styleUrl: './message.component.css'
})
export class MessageComponent {
  @Input() message!: Message;
  @Input() userName!: string;
}
