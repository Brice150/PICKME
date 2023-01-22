import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminUsersComponent } from './admin.users.component';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';



@NgModule({
  declarations: [AdminUsersComponent],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],
  exports : [AdminUsersComponent]
})
export class AdminUsersModule { }
