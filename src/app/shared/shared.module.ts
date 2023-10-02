import { NgModule } from '@angular/core';
import { AgePipe } from './pipes/age.pipe';
import { DescriptionPipe } from './pipes/description.pipe';
import { RolePipe } from './pipes/role.pipe';
import { CustomDatePipe } from './pipes/custom-date.pipe';

const declarations = [AgePipe, DescriptionPipe, RolePipe, CustomDatePipe];

@NgModule({
  declarations,
  imports: [],
  exports: declarations,
})
export class SharedModule {}
