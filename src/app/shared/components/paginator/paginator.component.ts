import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, input } from '@angular/core';

@Component({
    selector: 'app-paginator',
    imports: [CommonModule],
    templateUrl: './paginator.component.html',
    styleUrl: './paginator.component.css'
})
export class PaginatorComponent {
  page: number = 0;
  readonly loading = input<boolean>(true);
  readonly usersNumber = input<number>(0);
  readonly maxPerPage = input<number>(0);
  readonly maxPages = input<number>(100);
  @Output() handlePageEvent: EventEmitter<number> = new EventEmitter<number>();

  next(): void {
    if (
      !this.loading() &&
      this.usersNumber() === this.maxPerPage() &&
      this.page + 1 !== this.maxPages()
    ) {
      this.page++;
      this.handlePageEvent.emit(this.page);
    }
  }

  previous(): void {
    if (this.page !== 0) {
      this.page--;
      this.handlePageEvent.emit(this.page);
    }
  }
}
