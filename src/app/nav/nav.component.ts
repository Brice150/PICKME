import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { ConnectService } from '../core/services/connect.service';
import { NavAnimation } from './nav-animation';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css',
  animations: [NavAnimation],
})
export class NavComponent {
  isMenuActive: boolean = false;

  constructor(
    public connectService: ConnectService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  toggleMenu() {
    this.isMenuActive = !this.isMenuActive;
  }

  logout() {
    this.connectService.logout();
    this.router.navigate(['/']);
    this.toggleMenu();
    this.toastr.success('You are logged out', 'Logged Out', {
      positionClass: 'toast-bottom-center',
      toastClass: 'ngx-toastr custom',
    });
  }
}
