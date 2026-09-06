import type { Swiper } from 'swiper';
import type { SwiperContainer } from 'swiper/element';
import { NgClass } from '@angular/common';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  inject,
  input,
  linkedSignal,
  output,
  signal,
  viewChild,
} from '@angular/core';
import { environment } from '../../../environments/environment';
import { Picture } from '../../core/interfaces/picture';
import { User } from '../../core/interfaces/user';
import { ProfileService } from '../../core/services/profile.service';
import { PictureComponent } from './picture/picture.component';

@Component({
  selector: 'app-pictures',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgClass, PictureComponent],
  templateUrl: './pictures.component.html',
  styleUrl: './pictures.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class PicturesComponent {
  private readonly profileService = inject(ProfileService);

  readonly imagePath: string = environment.imagePath;
  readonly user = input<User>();
  readonly refreshEvent = output<string>();
  // Always in the template: the file input is what gets reset once a picture has been added.
  private readonly imageInput =
    viewChild.required<ElementRef<HTMLInputElement>>('imageInput');
  readonly isLoading = signal(false);
  readonly activeIndex = signal(0);

  /**
   * The album on screen. It follows the account it is read from, and an edition republishes it
   * rather than reaching into it: the carousel and the slides only see a value that has been
   * handed to them.
   */
  readonly pictures = linkedSignal<Picture[]>(
    () => this.user()?.pictures ?? [],
  );

  addPicture(files: File[]): void {
    for (const file of files) {
      this.isLoading.set(true);
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = (): void => {
        const img = new Image();
        img.src = reader.result as string;
        img.onload = () => {
          const maxDimension = 1200;
          const width = img.width;
          const height = img.height;
          let newWidth, newHeight;

          if (width > height) {
            newWidth = Math.min(width, maxDimension);
            newHeight = (height / width) * newWidth;
          } else {
            newHeight = Math.min(height, maxDimension);
            newWidth = (width / height) * newHeight;
          }

          const canvas = document.createElement('canvas');
          const ctx = canvas.getContext('2d');
          canvas.width = newWidth;
          canvas.height = newHeight;
          ctx!.drawImage(img, 0, 0, newWidth, newHeight);
          const quality = 0.7;
          const dataURL = canvas.toDataURL('image/jpeg', quality);

          this.profileService.addPicture(dataURL).subscribe({
            next: (picture: Picture) => {
              this.setPictures([picture, ...this.pictures()]);
              setTimeout(() => {
                this.getSwiper()?.update();
                this.getSwiper()?.slideTo(0);
                this.activeIndex.set(0);
                this.imageInput().nativeElement.value = '';
                this.refreshEvent.emit('Picture Added');
                this.isLoading.set(false);
              }, 0);
            },
          });
        };
      };
    }
  }

  deletePicture(pictureId: number): void {
    this.isLoading.set(true);
    this.profileService.deletePicture(pictureId).subscribe({
      next: () => {
        const deleted = this.pictures().find(
          (picture: Picture) => picture.id === pictureId,
        );
        if (!deleted) {
          return;
        }
        // Angular owns the slides: removing the picture from the album removes its slide, and
        // the carousel only recomputes its geometry once the view has caught up.
        const remaining = this.pictures().filter(
          (picture: Picture) => picture.id !== pictureId,
        );
        // The album is never left without a main picture.
        if (deleted.isMainPicture && remaining.length !== 0) {
          remaining[0] = { ...remaining[0], isMainPicture: true };
        }
        this.setPictures(remaining);
        setTimeout(() => {
          this.getSwiper()?.update();
          this.activeIndex.set(
            this.getSwiper()?.activeIndex ?? this.activeIndex(),
          );
        });
        this.refreshEvent.emit('Picture Deleted');
        this.isLoading.set(false);
      },
    });
  }

  selectMainPicture(pictureId: number): void {
    this.isLoading.set(true);
    this.profileService.selectMainPicture(pictureId).subscribe({
      next: () => {
        this.setPictures(
          this.pictures().map((picture: Picture) => ({
            ...picture,
            isMainPicture: picture.id === pictureId,
          })),
        );
        this.refreshEvent.emit('Main Picture Selected');
        this.isLoading.set(false);
      },
    });
  }

  onSlideChange(): void {
    this.activeIndex.set(this.getSwiper()?.activeIndex ?? this.activeIndex());
  }

  /**
   * Publishes an album, on the account as much as on the screen: the other panels of the profile
   * hold the same account object and read the pictures from it.
   */
  private setPictures(pictures: Picture[]): void {
    const user = this.user();
    if (user) {
      user.pictures = pictures;
    }
    this.pictures.set(pictures);
  }

  // Resolved from the template rather than from the whole document: a global query would find
  // the carousel of another screen, and the custom element is only upgraded after the first
  // render, which leaves the swiper instance undefined until then.
  private readonly carousel =
    viewChild<ElementRef<SwiperContainer>>('carousel');

  private getSwiper(): Swiper | undefined {
    return this.carousel()?.nativeElement.swiper;
  }
}
