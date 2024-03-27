import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Match } from '../../core/interfaces/match';
import { DescriptionPipe } from '../../shared/pipes/description.pipe';

@Component({
  selector: 'app-match-card',
  standalone: true,
  imports: [CommonModule, DescriptionPipe],
  templateUrl: './match-card.component.html',
  styleUrl: './match-card.component.css',
})
export class MatchCardComponent {
  imagePath: string = environment.imagePath;
  @Input() match!: Match;
  @Input() preview?: string;
  @Input() messageMode: boolean = false;
  @Output() clickEvent: EventEmitter<void> = new EventEmitter<void>();

  click(): void {
    this.clickEvent.emit();
  }

  isLastMessageFromMatch(): boolean {
    if (!this.match.messages || this.match.messages.length === 0) {
      return false;
    }
    const messagesWithContent = this.match.messages.filter(
      (message) => message.content
    );
    if (messagesWithContent.length === 0) {
      return false;
    }
    return (
      !this.messageMode &&
      messagesWithContent[messagesWithContent.length - 1].sender ===
        this.match.user.nickname
    );
  }
}
