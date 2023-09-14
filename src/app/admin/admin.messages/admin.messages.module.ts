import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminMessagesComponent } from './admin.messages.component';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  declarations: [AdminMessagesComponent],
  imports: [CommonModule, FormsModule, SharedModule],
  exports: [AdminMessagesComponent],
})
export class AdminMessagesModule {}
