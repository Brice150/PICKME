import { NgClass } from '@angular/common';
import { Component } from '@angular/core';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';

@Component({
  selector: 'app-connect',
  imports: [NgClass, LoginComponent, RegisterComponent],
  templateUrl: './connect.component.html',
  styleUrl: './connect.component.css',
})
export class ConnectComponent {
  isRegistering = false;

  toggleLoginOrRegister(page: string) {
    if (
      (page === 'login' && this.isRegistering) ||
      (page === 'register' && !this.isRegistering)
    ) {
      this.isRegistering = !this.isRegistering;
    }
  }
}
