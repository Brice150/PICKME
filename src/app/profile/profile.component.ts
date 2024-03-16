import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { User } from '../core/interfaces/user';
import { ConnectService } from '../core/services/connect.service';
import { ConnectionInfosComponent } from './connection-infos/connection-infos.component';
import { DeleteAccountComponent } from './delete-account/delete-account.component';
import { DescriptionComponent } from './description/description.component';
import { GenderAgeComponent } from './gender-age/gender-age.component';
import { MainInfosComponent } from './main-infos/main-infos.component';
import { PicturesComponent } from './pictures/pictures.component';
import { PreferencesComponent } from './preferences/preferences.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    MatExpansionModule,
    ConnectionInfosComponent,
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
    private toastr: ToastrService
  ) {}

  updateUser(message: string): void {
    this.connectService.connectedUser = this.user;
    //TODO: backend saved
    this.connectService.connectedUser!.password = undefined;
    this.user!.password = undefined;
    console.log(message);

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
    } else {
      this.toastr.success('Your profile has been updated', message, {
        positionClass: 'toast-bottom-center',
        toastClass: 'ngx-toastr custom',
      });
    }
  }

  deleteAccount(): void {
    this.connectService.connectedUser = undefined;
    this.user = undefined;
    //TODO: backend saved
    this.toastr.success('Your account has been deleted', 'Account Deleted', {
      positionClass: 'toast-bottom-center',
      toastClass: 'ngx-toastr custom',
    });
    this.router.navigate(['/']);
  }
}
