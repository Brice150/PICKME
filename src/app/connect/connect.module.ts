import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegisterModule } from './register/register.module';
import { LoginModule } from './login/login.module';
import { ConnectComponent } from './connect.component';



@NgModule({
  declarations: [ConnectComponent],
  imports: [
    CommonModule,
    RegisterModule,
    LoginModule
  ],
  exports: [ConnectComponent]
})
export class ConnectModule { }
