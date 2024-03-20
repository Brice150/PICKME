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
    return (
      !this.messageMode &&
      this.match.messages?.length !== 0 &&
      this.match.messages[this.match.messages.length - 1].sender ===
        this.match.user.nickname
    );
  }
}
