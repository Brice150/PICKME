import { NgClass } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from '@angular/core';

@Component({
  selector: 'app-paginator-demo',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgClass],
  templateUrl: './paginator-demo.component.html',
  styleUrl: './paginator-demo.component.css',
})
export class PaginatorDemoComponent {
  readonly currentIndex = input<number>(0);
  readonly listLength = input<number>(1);
  readonly previousEvent = output<void>();
  readonly nextEvent = output<void>();

  next(): void {
    if (this.currentIndex() !== this.listLength() - 1) {
      this.nextEvent.emit();
    }
  }

  previous(): void {
    if (this.currentIndex() !== 0) {
      this.previousEvent.emit();
    }
  }
}
