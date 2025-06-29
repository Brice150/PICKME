import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { Geolocation } from '../core/interfaces/geolocation';
import { User } from '../core/interfaces/user';
import { ConnectService } from '../core/services/connect.service';
import { ProfileService } from '../core/services/profile.service';
import { DeleteAccountComponent } from './delete-account/delete-account.component';
import { DescriptionComponent } from './description/description.component';
import { GenderAgeComponent } from './gender-age/gender-age.component';
import { MainInfosComponent } from './main-infos/main-infos.component';
import { PasswordComponent } from './password/password.component';
import { PicturesComponent } from './pictures/pictures.component';
import { PreferencesComponent } from './preferences/preferences.component';

@Component({
  selector: 'app-profile',
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
export class ProfileComponent implements OnInit, OnDestroy {
  connectService = inject(ConnectService);
  toastr = inject(ToastrService);
  profileService = inject(ProfileService);

  user?: User = { ...this.connectService.connectedUser! };
  destroyed$: Subject<void> = new Subject<void>();
  geolocation: Geolocation = {} as Geolocation;

  ngOnInit(): void {
    this.connectService
      .getGeolocation()
      .pipe(takeUntil(this.destroyed$))
      .subscribe({
        next: (geolocation: Geolocation) => {
          this.geolocation.latitude = geolocation.latitude;
          this.geolocation.longitude = geolocation.longitude;
          if (geolocation.city === geolocation.country_capital) {
            navigator.geolocation.getCurrentPosition(
              (position) => {
                this.geolocation.latitude = position.coords.latitude.toString();
                this.geolocation.longitude =
                  position.coords.longitude.toString();
              },
              (error) => {
                // Do nothing
              }
            );
          }
        },
      });
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  updateUser(message: string): void {
    this.user!.geolocation.latitude = this.geolocation.latitude;
    this.user!.geolocation.longitude = this.geolocation.longitude;
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
    this.connectService.getConnectedUser().subscribe({
      next: () => {
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
        this.user = undefined;
        this.connectService.logout();
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
