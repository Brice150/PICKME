import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectComponent } from './select.component';
import { HeaderModule } from '../header/header.module';
import { SharedModule } from '../shared/shared.module';



@NgModule({
  declarations: [SelectComponent],
  imports: [
    CommonModule,
    HeaderModule,
    SharedModule
  ],
  exports: [SelectComponent]
})
export class SelectModule { }
