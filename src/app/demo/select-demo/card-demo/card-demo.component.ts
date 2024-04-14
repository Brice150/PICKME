import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  DislikeButtonAnimation,
  LikeButtonAnimation,
  TextMatchAnimation,
} from '../../../select/card/card-animation';

@Component({
  selector: 'app-card-demo',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './card-demo.component.html',
  styleUrl: './card-demo.component.css',
  animations: [LikeButtonAnimation, DislikeButtonAnimation, TextMatchAnimation],
})
export class CardDemoComponent {
  @Input() display: boolean = false;
  @Input() image!: string;
  @Output() likeEvent: EventEmitter<void> = new EventEmitter<void>();
  @Output() dislikeEvent: EventEmitter<void> = new EventEmitter<void>();
  activeMatchAnimation: boolean = false;

  like(): void {
    if (this.image.includes('Picture2.jpg')) {
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
