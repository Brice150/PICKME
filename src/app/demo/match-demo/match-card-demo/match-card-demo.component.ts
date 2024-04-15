import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DescriptionPipe } from '../../../shared/pipes/description.pipe';

@Component({
  selector: 'app-match-card-demo',
  standalone: true,
  imports: [CommonModule, DescriptionPipe],
  templateUrl: './match-card-demo.component.html',
  styleUrl: './match-card-demo.component.css',
})
export class MatchCardDemoComponent {
  @Input() picture!: string;
  @Input() match!: string;
  @Input() preview?: string;
  @Output() clickEvent: EventEmitter<void> = new EventEmitter<void>();

  click(): void {
    this.clickEvent.emit();
  }
}
