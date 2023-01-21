import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LikeComponent } from './like.component';
import { HeaderModule } from '../header/header.module';



@NgModule({
  declarations: [LikeComponent],
  imports: [
    CommonModule,
    HeaderModule
  ],
  exports: [LikeComponent]
})
export class LikeModule { }
