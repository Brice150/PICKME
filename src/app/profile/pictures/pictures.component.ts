import { CommonModule } from '@angular/common';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  Component,
  ElementRef,
  EventEmitter,
  Output,
  ViewChild,
  input
} from '@angular/core';
import { environment } from '../../../environments/environment';
import { Picture } from '../../core/interfaces/picture';
import { User } from '../../core/interfaces/user';
import { ProfileService } from '../../core/services/profile.service';
import { PictureComponent } from './picture/picture.component';

@Component({
    selector: 'app-pictures',
    imports: [CommonModule, PictureComponent],
    templateUrl: './pictures.component.html',
    styleUrl: './pictures.component.css',
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PicturesComponent {
  imagePath: string = environment.imagePath;
  readonly user = input<User>();
  @Output() refreshEvent: EventEmitter<string> = new EventEmitter<string>();
  @ViewChild('imageInput') imageInput!: ElementRef;
  isLoading: boolean = false;

  constructor(private profileService: ProfileService) {}

  addPicture(files: File[]): void {
    for (let file of files) {
      this.isLoading = true;
      let reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = (event: any) => {
        const img = new Image();
        img.src = event.target.result;
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
          let quality = 0.7;
          let dataURL = canvas.toDataURL('image/jpeg', quality);

          this.profileService.addPicture(dataURL).subscribe({
            next: (picture: Picture) => {
              this.user()?.pictures?.unshift(picture);
              setTimeout(() => {
                document.querySelector('swiper-container')?.swiper.update();
                document.querySelector('swiper-container')?.swiper.slideTo(0);
                this.imageInput.nativeElement.value = '';
                this.refreshEvent.emit('Picture Added');
                this.isLoading = false;
              }, 0);
            },
          });
        };
      };
    }
  }

  deletePicture(pictureId: number): void {
    this.isLoading = true;
    this.profileService.deletePicture(pictureId).subscribe({
      next: () => {
        let isMainPictureDeleted: boolean = false;
        const pictureIndex = this.user()!.pictures!.findIndex(
          (picture: Picture) => picture.id === pictureId
        );
        if (pictureIndex !== -1) {
          isMainPictureDeleted =
            this.user()!.pictures![pictureIndex].isMainPicture;
          document
            .querySelector('swiper-container')
            ?.swiper.removeSlide(pictureIndex);
          this.user()!.pictures!.splice(pictureIndex, 1);
          const user = this.user();
          if (isMainPictureDeleted && user?.pictures?.length !== 0) {
            user!.pictures![0].isMainPicture = true;
          }
          this.refreshEvent.emit('Picture Deleted');
          this.isLoading = false;
        }
      },
    });
  }

  selectMainPicture(pictureId: number): void {
    this.isLoading = true;
    this.profileService.selectMainPicture(pictureId).subscribe({
      next: () => {
        const pictureIndex = this.user()!.pictures!.findIndex(
          (picture: Picture) => picture.id === pictureId
        );
        if (pictureIndex !== -1) {
          user?.pictures?.forEach(
            (picture: Picture) => (picture.isMainPicture = false)
          );
          user!.pictures![pictureIndex].isMainPicture = true;
        }
        this.refreshEvent.emit('Main Picture Selected');
        this.isLoading = false;
      },
    });
  }

  onSlideChange(): void {
    // Needed to update isCurrentView when we slide
  }

  isCurrentView(picture: Picture): boolean {
    const index: number | undefined =
      document.querySelector('swiper-container')?.swiper.activeIndex;
    if (index === undefined) {
      return false;
    }
    return picture.id === this.user()?.pictures![index].id;
  }
}
