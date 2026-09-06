import { NgClass } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from '@angular/core';
import { environment } from '../../../environments/environment';
import { Match } from '../../core/interfaces/match';
import { DescriptionPipe } from '../../shared/pipes/description.pipe';

@Component({
  selector: 'app-match-card',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgClass, DescriptionPipe],
  templateUrl: './match-card.component.html',
  styleUrl: './match-card.component.css',
})
export class MatchCardComponent {
  imagePath: string = environment.imagePath;
  readonly match = input.required<Match>();
  readonly preview = input<string>();
  readonly messageMode = input<boolean>(false);
  readonly clickEvent = output<void>();

  click(): void {
    this.clickEvent.emit();
  }

  isLastMessageFromMatch(): boolean {
    const match = this.match();
    if (!match.messages || match.messages.length === 0) {
      return false;
    }
    const messagesWithContent = match.messages.filter(
      (message) => message.content,
    );
    if (messagesWithContent.length === 0) {
      return false;
    }
    return (
      !this.messageMode() &&
      messagesWithContent[messagesWithContent.length - 1].sender ===
        match.user.nickname
    );
  }
}
