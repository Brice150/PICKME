import { Component, inject, input, output } from '@angular/core';
import { User } from '../../core/interfaces/user';
import { ConfirmationDialogComponent } from '../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { filter } from 'rxjs';

@Component({
  selector: 'app-delete-account',
  imports: [],
  templateUrl: './delete-account.component.html',
  styleUrl: './delete-account.component.css',
})
export class DeleteAccountComponent {
  private readonly dialog = inject(MatDialog);

  readonly user = input<User>();
  readonly deleteEvent = output<void>();

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
