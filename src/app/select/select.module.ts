import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectComponent } from './select.component';
import { HeaderModule } from '../header/header.module';
import { SharedModule } from '../shared/shared.module';
import { NgxUsefulSwiperModule } from 'ngx-useful-swiper';
import {MatSliderModule} from '@angular/material/slider';
import { FormsModule } from '@angular/forms';
import { AgePipe } from '../shared/pipes/age.pipe';


@NgModule({
  declarations: [SelectComponent],
  imports: [
    CommonModule,
    HeaderModule,
    SharedModule,
    NgxUsefulSwiperModule,
    MatSliderModule,
    FormsModule
  ],
  exports: [SelectComponent],
  providers: [AgePipe]
})
export class SelectModule { }
