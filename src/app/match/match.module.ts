import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatchComponent } from './match.component';
import { HeaderModule } from '../header/header.module';
import { MessageModule } from './message/message.module';



@NgModule({
  declarations: [MatchComponent],
  imports: [
    CommonModule,
    MessageModule,
    HeaderModule
  ],
  exports: [MatchComponent]
})
export class MatchModule { }
