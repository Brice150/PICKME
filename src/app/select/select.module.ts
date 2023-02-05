import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectComponent } from './select.component';
import { HeaderModule } from '../header/header.module';
import { SharedModule } from '../shared/shared.module';
import { NgxUsefulSwiperModule } from 'ngx-useful-swiper';



@NgModule({
  declarations: [SelectComponent],
  imports: [
    CommonModule,
    HeaderModule,
    SharedModule,
    NgxUsefulSwiperModule
  ],
  exports: [SelectComponent]
})
export class SelectModule { }
