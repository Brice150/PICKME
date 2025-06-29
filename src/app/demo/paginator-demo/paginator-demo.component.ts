import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
    selector: 'app-paginator-demo',
    imports: [CommonModule],
    templateUrl: './paginator-demo.component.html',
    styleUrl: './paginator-demo.component.css'
})
export class PaginatorDemoComponent {
  @Input() currentIndex: number = 0;
  @Input() listLength: number = 1;
  @Output() previousEvent: EventEmitter<void> = new EventEmitter<void>();
  @Output() nextEvent: EventEmitter<void> = new EventEmitter<void>();

  next(): void {
    if (this.currentIndex !== this.listLength - 1) {
      this.nextEvent.emit();
    }
  }

  previous(): void {
    if (this.currentIndex !== 0) {
      this.previousEvent.emit();
    }
  }
}
