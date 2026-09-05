import { NgClass } from '@angular/common';
import { Component, inject, input, output } from '@angular/core';
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
  imports: [NgClass],
  templateUrl: './picture.component.html',
  styleUrl: './picture.component.css',
  animations: [LikeButtonAnimation, DislikeButtonAnimation],
})
export class PictureComponent {
  private readonly dialog = inject(MatDialog);

  readonly picture = input.required<Picture>();
  readonly display = input<boolean>(false);
  readonly isLoading = input<boolean>(false);
  readonly selectMainEvent = output<void>();
  readonly deleteEvent = output<void>();

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
