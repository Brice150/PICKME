import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Output,
  inject,
  input,
} from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router, RouterModule } from '@angular/router';
import { filter } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Picture } from '../../core/interfaces/picture';
import { User } from '../../core/interfaces/user';
import { SelectService } from '../../core/services/select.service';
import { MoreInfoComponent } from '../../shared/components/more-info/more-info.component';
import { AgePipe } from '../../shared/pipes/age.pipe';
import { DescriptionPipe } from '../../shared/pipes/description.pipe';
import {
  ButtonMatchAnimation,
  DislikeButtonAnimation,
  LikeButtonAnimation,
  TextAnimation,
  TextMatchAnimation,
} from './card-animation';

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
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    TextAnimation,
    LikeButtonAnimation,
    DislikeButtonAnimation,
    TextMatchAnimation,
    ButtonMatchAnimation,
  ],
})
export class CardComponent {
  private readonly dialog = inject(MatDialog);
  private readonly router = inject(Router);
  private readonly selectService = inject(SelectService);

  imagePath: string = environment.imagePath;
  readonly user = input.required<User>();
  readonly display = input<boolean>(false);
  readonly activeMatchAnimation = input<boolean>(false);
  @Output() likeEvent: EventEmitter<void> = new EventEmitter<void>();
  @Output() dislikeEvent: EventEmitter<void> = new EventEmitter<void>();

  moreInfo(): void {
    const user = this.user();
    if (user.picturesLoaded || !user.id) {
      this.openMoreInfo(user);
      return;
    }
    this.selectService.getUserPictures(user.id).subscribe({
      next: (pictures: Picture[]) => {
        user.pictures = pictures;
        user.picturesLoaded = true;
        this.openMoreInfo(user);
      },
      error: () => this.openMoreInfo(user),
    });
  }

  private openMoreInfo(user: User): void {
    const dialogRef = this.dialog.open(MoreInfoComponent, {
      data: { user, adminMode: false, matchMode: false },
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

  viewMatch(): void {
    this.router.navigate(['match']);
  }
}
