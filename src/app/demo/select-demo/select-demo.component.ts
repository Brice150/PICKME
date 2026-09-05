import type { Swiper } from 'swiper';
import type { SwiperContainer } from 'swiper/element';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  Component,
  ElementRef,
  OnInit,
  input,
  viewChild,
} from '@angular/core';
import { Gender } from '../../core/enums/gender';
import { CardDemoComponent } from './card-demo/card-demo.component';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-select-demo',
  imports: [CardDemoComponent],
  templateUrl: './select-demo.component.html',
  styleUrl: './select-demo.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SelectDemoComponent implements OnInit {
  imagePath: string = environment.imagePath;
  readonly userGenderSearch = input.required<Gender>();
  images: string[] = [];

  ngOnInit(): void {
    const userGenderSearch = this.userGenderSearch();
    if (userGenderSearch === Gender.MAN) {
      this.imagePath = this.imagePath + 'man-select-demo/';
    } else if (userGenderSearch === Gender.WOMAN) {
      this.imagePath = this.imagePath + 'woman-select-demo/';
    }
    this.images = [
      this.imagePath + 'Picture1.jpg',
      this.imagePath + 'Picture2.jpg',
      this.imagePath + 'Picture3.jpg',
    ];
  }

  isCurrentView(image: string): boolean {
    const index: number | undefined = this.getSwiper()?.activeIndex;
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
      (image: string) => image === imageToRemove,
    );
    if (imageIndex !== -1) {
      this.getSwiper()?.removeSlide(imageIndex);
      this.images.splice(imageIndex, 1);
    }
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
