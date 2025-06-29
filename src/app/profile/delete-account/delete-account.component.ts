import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, input, inject } from '@angular/core';
import { User } from '../../core/interfaces/user';
import { ConfirmationDialogComponent } from '../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { filter } from 'rxjs';

@Component({
    selector: 'app-delete-account',
    imports: [CommonModule],
    templateUrl: './delete-account.component.html',
    styleUrl: './delete-account.component.css'
})
export class DeleteAccountComponent {
  dialog = inject(MatDialog);

  readonly user = input<User>();
  @Output() deleteEvent: EventEmitter<void> = new EventEmitter<void>();

  openDialog(): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: 'delete your account',
    });

    dialogRef
      .afterClosed()
      .pipe(filter((res: boolean) => res))
      .subscribe(() => {
        this.deleteEvent.emit();
      });
  }
}
