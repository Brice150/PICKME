import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ConnectService } from '../../core/services/connect.service';
import { Router } from '@angular/router';
import { User } from '../../core/interfaces/user';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-start-demo',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './start-demo.component.html',
  styleUrl: './start-demo.component.css',
})
export class StartDemoComponent {
  constructor(
    private connectService: ConnectService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  startNow(): void {
    if (this.connectService.registeredUser) {
      this.loginUser(this.connectService.registeredUser);
    } else {
      this.router.navigate(['/']);
    }
  }

  loginUser(user: User): void {
    this.connectService.login(user).subscribe({
      next: () => {
        this.router.navigate(['/profile']);
      },
      complete: () => {
        this.toastr.success('You are logged in !', 'Logged In', {
          positionClass: 'toast-bottom-center',
          toastClass: 'ngx-toastr custom gold',
        });
      },
    });
  }
}
