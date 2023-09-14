import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileComponent } from './profile.component';
import { HeaderModule } from '../header/header.module';
import { ReactiveFormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';

@NgModule({
  declarations: [ProfileComponent],
  imports: [CommonModule, HeaderModule, ReactiveFormsModule, MatChipsModule],
  exports: [ProfileComponent],
})
export class ProfileModule {}
