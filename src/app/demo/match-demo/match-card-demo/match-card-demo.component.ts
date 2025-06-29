import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, input } from '@angular/core';
import { DescriptionPipe } from '../../../shared/pipes/description.pipe';

@Component({
    selector: 'app-match-card-demo',
    imports: [CommonModule, DescriptionPipe],
    templateUrl: './match-card-demo.component.html',
    styleUrl: './match-card-demo.component.css'
})
export class MatchCardDemoComponent {
  readonly picture = input.required<string>();
  readonly match = input.required<string>();
  readonly preview = input<string>();
  @Output() clickEvent: EventEmitter<void> = new EventEmitter<void>();

  click(): void {
    this.clickEvent.emit();
  }
}
