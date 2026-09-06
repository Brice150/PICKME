import { NgClass } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
  signal,
} from '@angular/core';
import {
  DislikeButtonAnimation,
  LikeButtonAnimation,
  TextMatchAnimation,
} from '../../../select/card/card-animation';

@Component({
  selector: 'app-card-demo',
  changeDetection: ChangeDetectionStrategy.OnPush,
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
  readonly activeMatchAnimation = signal(false);

  like(): void {
    if (this.image().includes('Picture2.jpg')) {
      this.activeMatchAnimation.set(true);
      setTimeout(() => {
        this.activeMatchAnimation.set(false);
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
