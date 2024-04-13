import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { User } from '../../core/interfaces/user';
import { environment } from '../../../environments/environment';
import { MoreInfoComponent } from '../../shared/components/more-info/more-info.component';
import { MatDialog } from '@angular/material/dialog';
import { filter } from 'rxjs';
import { CustomDatePipe } from '../../shared/pipes/custom-date.pipe';
import { DescriptionPipe } from '../../shared/pipes/description.pipe';

@Component({
  selector: 'app-user-card',
  standalone: true,
  imports: [CommonModule, CustomDatePipe, DescriptionPipe],
  templateUrl: './user-card.component.html',
  styleUrl: './user-card.component.css',
})
export class UserCardComponent {
  imagePath: string = environment.imagePath;
  @Input() user!: User;
  @Output() deleteEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor(public dialog: MatDialog) {}

  moreInfo(): void {
    const dialogRef = this.dialog.open(MoreInfoComponent, {
      data: { user: this.user, adminMode: true, matchMode: false },
    });

    dialogRef
      .afterClosed()
      .pipe(filter((res: boolean) => res))
      .subscribe(() => {
        this.deleteEvent.emit();
      });
  }
}
