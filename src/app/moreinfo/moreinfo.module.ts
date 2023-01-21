import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MoreInfoComponent } from './moreinfo.component';
import { HeaderModule } from '../header/header.module';
import { RouterModule } from '@angular/router';



@NgModule({
  declarations: [MoreInfoComponent],
  imports: [
    CommonModule,
    HeaderModule,
    RouterModule
  ],
  exports: [MoreInfoComponent]
})
export class MoreinfoModule { }
