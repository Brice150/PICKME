import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { filter } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../../core/interfaces/user';
import { MoreInfoComponent } from '../../shared/components/more-info/more-info.component';
import { AgePipe } from '../../shared/pipes/age.pipe';
import { DescriptionPipe } from '../../shared/pipes/description.pipe';
import {
  DislikeButtonAnimation,
  LikeButtonAnimation,
  ButtonMatchAnimation,
  TextAnimation,
  TextMatchAnimation,
} from './card-animation';
import { Router, RouterModule } from '@angular/router';

@Component({
    selector: 'app-card',
    imports: [
        CommonModule,
        DescriptionPipe,
        AgePipe,
        MatDialogModule,
        RouterModule,
    ],
    templateUrl: './card.component.html',
    styleUrl: './card.component.css',
    animations: [
        TextAnimation,
        LikeButtonAnimation,
        DislikeButtonAnimation,
        TextMatchAnimation,
        ButtonMatchAnimation,
    ]
})
export class CardComponent {
  imagePath: string = environment.imagePath;
  @Input() user!: User;
  @Input() display: boolean = false;
  @Output() likeEvent: EventEmitter<void> = new EventEmitter<void>();
  @Output() dislikeEvent: EventEmitter<void> = new EventEmitter<void>();
  @Input() activeMatchAnimation: boolean = false;

  constructor(public dialog: MatDialog, private router: Router) {}

  moreInfo(): void {
    const dialogRef = this.dialog.open(MoreInfoComponent, {
      data: { user: this.user, adminMode: false, matchMode: false },
    });

    dialogRef
      .afterClosed()
      .pipe(filter((res: string) => !!res))
      .subscribe((res: string) => {
        if (res === 'like') {
          this.like();
        } else {
          this.dislike();
        }
      });
  }

  like(): void {
    this.likeEvent.emit();
  }

  dislike(): void {
    this.dislikeEvent.emit();
  }

  viewMatch() {
    this.router.navigate(['match']);
  }
}
