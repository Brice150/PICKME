import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
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
  standalone: true,
  imports: [CommonModule],
  templateUrl: './picture.component.html',
  styleUrl: './picture.component.css',
  animations: [LikeButtonAnimation, DislikeButtonAnimation],
})
export class PictureComponent {
  @Input() picture!: Picture;
  @Input() display: boolean = false;
  @Output() selectMainEvent: EventEmitter<void> = new EventEmitter<void>();
  @Output() deleteEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor(public dialog: MatDialog) {}

  selectMainPicture(): void {
    if (!this.picture.isMainPicture) {
      this.selectMainEvent.emit();
    }
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
