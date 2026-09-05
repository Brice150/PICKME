import { NgClass } from '@angular/common';
import { Component, input, output } from '@angular/core';
import {
  DislikeButtonAnimation,
  LikeButtonAnimation,
  TextMatchAnimation,
} from '../../../select/card/card-animation';

@Component({
  selector: 'app-card-demo',
  imports: [NgClass],
  templateUrl: './card-demo.component.html',
  styleUrl: './card-demo.component.css',
  animations: [LikeButtonAnimation, DislikeButtonAnimation, TextMatchAnimation],
})
export class CardDemoComponent {
  readonly display = input<boolean>(false);
  readonly image = input.required<string>();
  readonly likeEvent = output<void>();
  readonly dislikeEvent = output<void>();
  activeMatchAnimation = false;

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
