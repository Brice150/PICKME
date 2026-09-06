import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ConnectService } from '../../core/services/connect.service';
import { Router } from '@angular/router';
import { User } from '../../core/interfaces/user';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-start-demo',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  templateUrl: './start-demo.component.html',
  styleUrl: './start-demo.component.css',
})
export class StartDemoComponent {
  private readonly connectService = inject(ConnectService);
  private readonly router = inject(Router);
  private readonly toastr = inject(ToastrService);

  startNow(): void {
    const registeredUser = this.connectService.registeredUser();
    if (registeredUser) {
      this.loginUser(registeredUser);
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
