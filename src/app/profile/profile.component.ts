import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatExpansionModule } from '@angular/material/expansion';
import { ToastrService } from 'ngx-toastr';
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
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
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
export class ProfileComponent implements OnInit {
  private readonly connectService = inject(ConnectService);
  private readonly toastr = inject(ToastrService);
  private readonly profileService = inject(ProfileService);
  private readonly destroyRef = inject(DestroyRef);

  /**
   * The account the panels edit, a copy of the connected one: an abandoned edition has to be
   * able to fall back on what the server holds.
   */
  readonly user = signal<User | undefined>({
    ...this.connectService.connectedUser()!,
  });
  geolocation: Geolocation = {} as Geolocation;

  ngOnInit(): void {
    this.connectService
      .getGeolocation()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (geolocation: Geolocation) => {
          this.geolocation.latitude = geolocation.latitude;
          this.geolocation.longitude = geolocation.longitude;
          if (geolocation.city === geolocation.country_capital) {
            navigator.geolocation.getCurrentPosition((position) => {
              this.geolocation.latitude = position.coords.latitude.toString();
              this.geolocation.longitude = position.coords.longitude.toString();
            });
          }
        },
      });
  }

  updateUser(message: string): void {
    const user = this.user()!;
    user.geolocation.latitude = this.geolocation.latitude;
    user.geolocation.longitude = this.geolocation.longitude;
    this.profileService.updateUser(user).subscribe({
      next: (updatedUser: User) => {
        user.password = undefined;
        this.connectService.connectedUser.set(updatedUser);
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
        this.user.set(undefined);
        this.connectService.logout();
      },
      complete: () => {
        this.toastr.success(
          'Your account has been deleted',
          'Account Deleted',
          {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          },
        );
      },
    });
  }
}
