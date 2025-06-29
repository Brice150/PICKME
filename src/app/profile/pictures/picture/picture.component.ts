import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { filter } from 'rxjs';
import { Picture } from '../../../core/interfaces/picture';
import { ConfirmationDialogComponent } from '../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import {
  DislikeButtonAnimation,
  LikeButtonAnimation,
} from '../../../select/card/card-animation';

@Component({
    selector: 'app-picture',
    imports: [CommonModule],
    templateUrl: './picture.component.html',
    styleUrl: './picture.component.css',
    animations: [LikeButtonAnimation, DislikeButtonAnimation]
})
export class PictureComponent {
  readonly picture = input.required<Picture>();
  readonly display = input<boolean>(false);
  readonly isLoading = input<boolean>(false);
  @Output() selectMainEvent: EventEmitter<void> = new EventEmitter<void>();
  @Output() deleteEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor(public dialog: MatDialog) {}

  selectMainPicture(): void {
    if (!this.picture().isMainPicture && !this.isLoading()) {
      this.selectMainEvent.emit();
    }
  }

  openDialog(): void {
    if (!this.isLoading()) {
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
}
