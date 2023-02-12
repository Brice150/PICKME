import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageComponent } from './message.component';
import { SharedModule } from 'src/app/shared/shared.module';



@NgModule({
  declarations: [MessageComponent],
  imports: [
    CommonModule,
    SharedModule
  ],
  exports: [MessageComponent]
})
export class MessageModule { }
