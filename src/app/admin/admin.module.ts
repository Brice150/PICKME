import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminComponent } from './admin.component';
import { HeaderModule } from '../header/header.module';
import { AdminMessagesModule } from './admin.messages/admin.messages.module';
import { AdminUsersModule } from './admin.users/admin.users.module';



@NgModule({
  declarations: [AdminComponent],
  imports: [
    CommonModule,
    HeaderModule,
    AdminMessagesModule,
    AdminUsersModule
  ],
  exports: [AdminComponent]
})
export class AdminModule { }
