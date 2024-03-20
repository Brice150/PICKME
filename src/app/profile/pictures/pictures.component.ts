import { CommonModule } from '@angular/common';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import { environment } from '../../../environments/environment';
import { Picture } from '../../core/interfaces/picture';
import { User } from '../../core/interfaces/user';
import { PictureComponent } from './picture/picture.component';
import { ToastrService } from 'ngx-toastr';
import { ProfileService } from '../../core/services/profile.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-pictures',
  standalone: true,
  imports: [CommonModule, PictureComponent],
  templateUrl: './pictures.component.html',
  styleUrl: './pictures.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class PicturesComponent {
  imagePath: string = environment.imagePath;
  @Input() user?: User;
  @Output() refreshEvent: EventEmitter<string> = new EventEmitter<string>();
  @ViewChild('imageInput') imageInput!: ElementRef;

  constructor(
    private toastr: ToastrService,
    private profileService: ProfileService
  ) {}

  addPicture(files: File[]): void {
    let mainPictureExists: boolean = false;
    if (this.user?.mainPicture) {
      mainPictureExists = true;
    }
    if (files) {
      let reader = new FileReader();
      reader.readAsDataURL(files[0]);
      reader.onload = (event: any) => {
        if (
          this.user?.pictures?.some(
            (picture: Picture) => picture.content === event.target.result
          )
        ) {
          this.imageInput.nativeElement.value = '';
          this.toastr.error('Picture is already in profile', 'Change Picture', {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          });
        } else if (this.user!.pictures!.length > 5) {
          this.imageInput.nativeElement.value = '';
          this.toastr.error(
            'You already have 6 pictures',
            'Max Pictures Number',
            {
              positionClass: 'toast-bottom-center',
              toastClass: 'ngx-toastr custom',
            }
          );
        } else {
          this.profileService.addPicture(event.target.result).subscribe({
            next: (picture: Picture) => {
              this.user?.pictures?.unshift(picture);
              setTimeout(() => {
                document.querySelector('swiper-container')?.swiper.update();
                document.querySelector('swiper-container')?.swiper.slideTo(0);
                this.imageInput.nativeElement.value = '';
                this.refreshEvent.emit('Picture Added');
              }, 0);
            },
            error: (error: HttpErrorResponse) => {
              this.toastr.error(error.message, 'Error', {
                positionClass: 'toast-bottom-center',
                toastClass: 'ngx-toastr custom',
              });
            },
          });
        }
      };
    }
  }

  deletePicture(pictureId: number): void {
    this.profileService.deletePicture(pictureId).subscribe({
      next: () => {
        let isMainPictureDeleted: boolean = false;
        const pictureIndex = this.user!.pictures!.findIndex(
          (picture: Picture) => picture.id === pictureId
        );
        if (pictureIndex !== -1) {
          isMainPictureDeleted =
            this.user!.pictures![pictureIndex].isMainPicture;
          document
            .querySelector('swiper-container')
            ?.swiper.removeSlide(pictureIndex);
          this.user!.pictures!.splice(pictureIndex, 1);
          if (isMainPictureDeleted && this.user?.pictures?.length !== 0) {
            this.user!.pictures![0].isMainPicture = true;
          }
          this.refreshEvent.emit('Picture Deleted');
        }
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, 'Error', {
          positionClass: 'toast-bottom-center',
          toastClass: 'ngx-toastr custom',
        });
      },
    });
  }

  selectMainPicture(pictureId: number): void {
    this.profileService.selectMainPicture(pictureId).subscribe({
      next: () => {
        const pictureIndex = this.user!.pictures!.findIndex(
          (picture: Picture) => picture.id === pictureId
        );
        if (pictureIndex !== -1) {
          this.user?.pictures?.forEach(
            (picture: Picture) => (picture.isMainPicture = false)
          );
          this.user!.pictures![pictureIndex].isMainPicture = true;
        }
        this.refreshEvent.emit('Main Picture Selected');
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, 'Error', {
          positionClass: 'toast-bottom-center',
          toastClass: 'ngx-toastr custom',
        });
      },
    });
  }
}
