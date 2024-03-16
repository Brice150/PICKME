import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Picture } from '../../../core/interfaces/picture';
import { ConfirmationDialogComponent } from '../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { filter } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-picture',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './picture.component.html',
  styleUrl: './picture.component.css',
})
export class PictureComponent {
  @Input() picture!: Picture;
  @Output() selectMainEvent: EventEmitter<void> = new EventEmitter<void>();
  @Output() deleteEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor(public dialog: MatDialog) {}

  selectMainPicture(): void {
    this.selectMainEvent.emit();
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: 'delete this picture',
    });

    dialogRef
      .afterClosed()
      .pipe(filter((res: boolean) => res))
      .subscribe(() => {
        this.deleteEvent.emit();
      });
  }
}
