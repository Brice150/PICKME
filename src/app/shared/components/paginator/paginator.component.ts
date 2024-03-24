import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';

@Component({
  selector: 'app-paginator',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './paginator.component.html',
  styleUrl: './paginator.component.css',
})
export class PaginatorComponent {
  page: number = 0;
  @Input() loading: boolean = true;
  @Input() usersNumber: number = 0;
  @Output() handlePageEvent: EventEmitter<number> = new EventEmitter<number>();

  next(): void {
    if (!this.loading && this.usersNumber === 25) {
      this.page = this.page + 1;
      this.handlePageEvent.emit(this.page);
    }
  }

  previous(): void {
    if (this.page !== 0) {
      this.page = this.page - 1;
      this.handlePageEvent.emit(this.page);
    }
  }
}
