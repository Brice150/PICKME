import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { User } from '../core/interfaces/user';
import { ConnectService } from '../core/services/connect.service';
import { PasswordComponent } from './password/password.component';
import { DeleteAccountComponent } from './delete-account/delete-account.component';
import { DescriptionComponent } from './description/description.component';
import { GenderAgeComponent } from './gender-age/gender-age.component';
import { MainInfosComponent } from './main-infos/main-infos.component';
import { PicturesComponent } from './pictures/pictures.component';
import { PreferencesComponent } from './preferences/preferences.component';
import { ProfileService } from '../core/services/profile.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    MatExpansionModule,
    PasswordComponent,
    DescriptionComponent,
    GenderAgeComponent,
    MainInfosComponent,
    PicturesComponent,
    PreferencesComponent,
    DeleteAccountComponent,
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
})
export class ProfileComponent {
  user?: User = this.connectService.connectedUser;

  constructor(
    private connectService: ConnectService,
    private router: Router,
    private toastr: ToastrService,
    private profileService: ProfileService
  ) {}

  updateUser(message: string): void {
    this.profileService.updateUser(this.user!).subscribe({
      next: (updatedUser: User) => {
        this.user!.password = undefined;
        this.connectService.connectedUser = updatedUser;
      },
      complete: () => {
        this.toastr.success('Your profile has been updated', message, {
          positionClass: 'toast-bottom-center',
          toastClass: 'ngx-toastr custom',
        });
      },
    });
  }

  refreshUser(message: string): void {
    this.profileService.getConnectedUser().subscribe({
      next: (user: User) => {
        this.connectService.connectedUser = user;
      },
      complete: () => {
        if (message && message === 'Main Picture Selected') {
          this.toastr.success('Your main picture has been selected', message, {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom gold',
          });
        } else if (message && message === 'Picture Deleted') {
          this.toastr.success('Picture has been deleted', message, {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          });
        } else if (message && message === 'Picture Added') {
          this.toastr.success('Picture has been added', message, {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          });
        }
      },
    });
  }

  deleteAccount(): void {
    this.profileService.deleteConnectedUser().subscribe({
      next: () => {
        this.connectService.connectedUser = undefined;
        this.user = undefined;
        this.router.navigate(['/']);
      },
      complete: () => {
        this.toastr.success(
          'Your account has been deleted',
          'Account Deleted',
          {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          }
        );
      },
    });
  }
}
