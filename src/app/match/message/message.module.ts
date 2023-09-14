import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageComponent } from './message.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [MessageComponent],
  imports: [CommonModule, SharedModule, ReactiveFormsModule],
  exports: [MessageComponent],
})
export class MessageModule {}
