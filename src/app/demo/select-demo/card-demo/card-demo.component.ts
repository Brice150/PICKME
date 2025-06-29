import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, input } from '@angular/core';
import {
  DislikeButtonAnimation,
  LikeButtonAnimation,
  TextMatchAnimation,
} from '../../../select/card/card-animation';

@Component({
    selector: 'app-card-demo',
    imports: [CommonModule],
    templateUrl: './card-demo.component.html',
    styleUrl: './card-demo.component.css',
    animations: [LikeButtonAnimation, DislikeButtonAnimation, TextMatchAnimation]
})
export class CardDemoComponent {
  readonly display = input<boolean>(false);
  readonly image = input.required<string>();
  @Output() likeEvent: EventEmitter<void> = new EventEmitter<void>();
  @Output() dislikeEvent: EventEmitter<void> = new EventEmitter<void>();
  activeMatchAnimation: boolean = false;

  like(): void {
    if (this.image().includes('Picture2.jpg')) {
      this.activeMatchAnimation = true;
      setTimeout(() => {
        this.activeMatchAnimation = false;
        this.likeEvent.emit();
      }, 2000);
    } else {
      this.likeEvent.emit();
    }
  }

  dislike(): void {
    this.dislikeEvent.emit();
  }
}
