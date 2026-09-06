import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  output,
} from '@angular/core';
import { User } from '../../core/interfaces/user';
import { environment } from '../../../environments/environment';
import { MoreInfoComponent } from '../../shared/components/more-info/more-info.component';
import { MatDialog } from '@angular/material/dialog';
import { filter } from 'rxjs';
import { CustomDatePipe } from '../../shared/pipes/custom-date.pipe';
import { DescriptionPipe } from '../../shared/pipes/description.pipe';

@Component({
  selector: 'app-user-card',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CustomDatePipe, DescriptionPipe],
  templateUrl: './user-card.component.html',
  styleUrl: './user-card.component.css',
})
export class UserCardComponent {
  private readonly dialog = inject(MatDialog);

  imagePath: string = environment.imagePath;
  readonly user = input.required<User>();
  readonly deleteEvent = output<void>();

  moreInfo(): void {
    const dialogRef = this.dialog.open(MoreInfoComponent, {
      data: { user: this.user(), adminMode: true, matchMode: false },
    });

    dialogRef
      .afterClosed()
      .pipe(filter((res: boolean) => res))
      .subscribe(() => {
        this.deleteEvent.emit();
      });
  }
}
