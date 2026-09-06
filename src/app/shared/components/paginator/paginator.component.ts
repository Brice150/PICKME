import { NgClass } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
  signal,
} from '@angular/core';

@Component({
  selector: 'app-paginator',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgClass],
  templateUrl: './paginator.component.html',
  styleUrl: './paginator.component.css',
})
export class PaginatorComponent {
  readonly page = signal(0);
  readonly loading = input<boolean>(true);
  readonly usersNumber = input<number>(0);
  readonly maxPerPage = input<number>(0);
  readonly maxPages = input<number>(100);
  readonly handlePageEvent = output<number>();

  next(): void {
    if (
      !this.loading() &&
      this.usersNumber() === this.maxPerPage() &&
      this.page() + 1 !== this.maxPages()
    ) {
      this.page.update((page: number) => page + 1);
      this.handlePageEvent.emit(this.page());
    }
  }

  previous(): void {
    if (this.page() !== 0) {
      this.page.update((page: number) => page - 1);
      this.handlePageEvent.emit(this.page());
    }
  }
}
