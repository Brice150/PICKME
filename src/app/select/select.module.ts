import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectComponent } from './select.component';
import { HeaderModule } from '../header/header.module';



@NgModule({
  declarations: [SelectComponent],
  imports: [
    CommonModule,
    HeaderModule
  ],
  exports: [SelectComponent]
})
export class SelectModule { }
