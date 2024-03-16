import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { environment } from '../../../environments/environment';
import { User } from '../../core/interfaces/user';
import { MoreInfoComponent } from '../../shared/components/more-info/more-info.component';
import { AgePipe } from '../../shared/pipes/age.pipe';
import { DescriptionPipe } from '../../shared/pipes/description.pipe';
import {
  LikeButtonAnimation,
  LogoMatchAnimation,
  TextMatchAnimation,
  DislikeButtonAnimation,
  TextAnimation,
} from './card-animation';
import { filter } from 'rxjs';

@Component({
  selector: 'app-card',
  standalone: true,
  imports: [CommonModule, DescriptionPipe, AgePipe, MatDialogModule],
  templateUrl: './card.component.html',
  styleUrl: './card.component.css',
  animations: [
    TextAnimation,
    LikeButtonAnimation,
    DislikeButtonAnimation,
    TextMatchAnimation,
    LogoMatchAnimation,
  ],
})
export class CardComponent {
  imagePath: string = environment.imagePath;
  @Input() user!: User;
  @Input() display: boolean = false;
  @Output() likeEvent: EventEmitter<string> = new EventEmitter<string>();
  @Output() dislikeEvent: EventEmitter<void> = new EventEmitter<void>();
  activeMatchAnimation: boolean = false;

  constructor(public dialog: MatDialog) {}

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
    if (this.user.gold) {
      this.activeMatchAnimation = true;
      this.display = false;
      setTimeout(() => {
        this.activeMatchAnimation = false;
        this.likeEvent.emit('match');
      }, 2000);
    } else {
      this.likeEvent.emit('like');
    }
  }

  dislike(): void {
    this.dislikeEvent.emit();
  }
}
