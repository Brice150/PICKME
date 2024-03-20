import { CommonModule } from '@angular/common';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  Component,
  Inject,
  OnInit,
} from '@angular/core';
import { environment } from '../../../../environments/environment';
import { User } from '../../../core/interfaces/user';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatChipsModule } from '@angular/material/chips';
import { AgePipe } from '../../pipes/age.pipe';
import {
  LikeButtonAnimation,
  DislikeButtonAnimation,
} from '../../../select/card/card-animation';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { filter } from 'rxjs';

@Component({
  selector: 'app-more-info',
  standalone: true,
  imports: [CommonModule, MatChipsModule, AgePipe],
  templateUrl: './more-info.component.html',
  styleUrl: './more-info.component.css',
  animations: [LikeButtonAnimation, DislikeButtonAnimation],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class MoreInfoComponent implements OnInit {
  imagePath: string = environment.imagePath;
  user!: User;
  adminMode: boolean = false;
  matchMode: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<MoreInfoComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.user = this.data.user;
    this.adminMode = this.data.adminMode;
    this.matchMode = this.data.matchMode;
  }

  close(): void {
    this.dialogRef.close();
  }

  like(): void {
    this.dialogRef.close('like');
  }

  dislike(): void {
    if (!this.matchMode) {
      this.dialogRef.close('dislike');
    } else {
      const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
        data: 'dislike ' + this.user.nickname,
      });

      dialogRef
        .afterClosed()
        .pipe(filter((res: boolean) => res))
        .subscribe(() => {
          this.dialogRef.close(true);
        });
    }
  }

  deleteUser(): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: 'delete this user',
    });

    dialogRef
      .afterClosed()
      .pipe(filter((res: boolean) => res))
      .subscribe(() => {
        this.dialogRef.close(true);
      });
  }
}
