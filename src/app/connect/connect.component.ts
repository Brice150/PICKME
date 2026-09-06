import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';

@Component({
  selector: 'app-connect',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgClass, LoginComponent, RegisterComponent],
  templateUrl: './connect.component.html',
  styleUrl: './connect.component.css',
})
export class ConnectComponent {
  readonly isRegistering = signal(false);

  toggleLoginOrRegister(page: string) {
    if (
      (page === 'login' && this.isRegistering()) ||
      (page === 'register' && !this.isRegistering())
    ) {
      this.isRegistering.update((registering: boolean) => !registering);
    }
  }
}
