import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MoreInfoComponent } from './moreinfo.component';
import { HeaderModule } from '../header/header.module';
import { RouterModule } from '@angular/router';
import { MatChipsModule } from '@angular/material/chips';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [MoreInfoComponent],
  imports: [
    CommonModule,
    HeaderModule,
    RouterModule,
    MatChipsModule,
    SharedModule
  ],
  exports: [MoreInfoComponent]
})
export class MoreinfoModule { }
