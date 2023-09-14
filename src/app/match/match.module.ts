import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatchComponent } from './match.component';
import { HeaderModule } from '../header/header.module';
import { MessageModule } from './message/message.module';
import { NgxUsefulSwiperModule } from 'ngx-useful-swiper';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [MatchComponent],
  imports: [
    CommonModule,
    MessageModule,
    HeaderModule,
    NgxUsefulSwiperModule,
    FormsModule,
  ],
  exports: [MatchComponent],
})
export class MatchModule {}
