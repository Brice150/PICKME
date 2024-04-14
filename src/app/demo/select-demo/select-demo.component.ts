import { CommonModule } from '@angular/common';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  Component,
  Input,
  OnInit,
} from '@angular/core';
import { Gender } from '../../core/enums/gender';
import { CardDemoComponent } from './card-demo/card-demo.component';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-select-demo',
  standalone: true,
  imports: [CommonModule, CardDemoComponent],
  templateUrl: './select-demo.component.html',
  styleUrl: './select-demo.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SelectDemoComponent implements OnInit {
  imagePath: string = environment.imagePath;
  @Input() userGenderSearch!: Gender;
  images: string[] = [];

  ngOnInit(): void {
    if (this.userGenderSearch === Gender.MAN) {
      this.imagePath = this.imagePath + 'man-select-demo/';
    } else if (this.userGenderSearch === Gender.WOMAN) {
      this.imagePath = this.imagePath + 'woman-select-demo/';
    }
    this.images = [
      this.imagePath + 'Picture1.jpg',
      this.imagePath + 'Picture2.jpg',
      this.imagePath + 'Picture3.jpg',
    ];
  }

  isCurrentView(image: string): boolean {
    const index: number | undefined =
      document.querySelector('swiper-container')?.swiper.activeIndex;
    if (index === undefined) {
      return false;
    }
    return image === this.images[index];
  }

  onSlideChange(): void {
    //Needed to update slide swiper index
  }

  like(image: string): void {
    this.removeSlide(image);
  }

  dislike(image: string): void {
    this.removeSlide(image);
  }

  removeSlide(imageToRemove: string): void {
    const imageIndex = this.images.findIndex(
      (image: string) => image === imageToRemove
    );
    if (imageIndex !== -1) {
      document
        .querySelector('swiper-container')
        ?.swiper.removeSlide(imageIndex);
      this.images.splice(imageIndex, 1);
    }
  }
}
