import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LikeComponent } from './like.component';
import { HeaderModule } from '../header/header.module';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [LikeComponent],
  imports: [CommonModule, HeaderModule, SharedModule],
  exports: [LikeComponent],
})
export class LikeModule {}
