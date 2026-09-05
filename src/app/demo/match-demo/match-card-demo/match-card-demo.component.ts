import { NgClass } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { DescriptionPipe } from '../../../shared/pipes/description.pipe';

@Component({
  selector: 'app-match-card-demo',
  imports: [NgClass, DescriptionPipe],
  templateUrl: './match-card-demo.component.html',
  styleUrl: './match-card-demo.component.css',
})
export class MatchCardDemoComponent {
  readonly picture = input.required<string>();
  readonly match = input.required<string>();
  readonly preview = input<string>();
  readonly clickEvent = output<void>();

  click(): void {
    this.clickEvent.emit();
  }
}
