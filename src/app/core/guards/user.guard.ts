import { CanActivateFn, Router } from '@angular/router';
import { ConnectService } from '../services/connect.service';
import { inject } from '@angular/core';
import { ProfileService } from '../services/profile.service';
import { User } from '../interfaces/user';
import { Observable } from 'rxjs';

export const userGuard: CanActivateFn = (route, state) => {
  const connectService = inject(ConnectService);
  const profileService = inject(ProfileService);
  const router = inject(Router);

  if (connectService.connectedUser) {
    return true;
  } else {
    return new Observable<boolean>((observer) => {
      profileService.getConnectedUser().subscribe({
        next: (user: User) => {
          connectService.connectedUser = user;
          observer.next(true);
          observer.complete();
        },
        error: () => {
          router.navigate(['/']);
          observer.next(false);
          observer.complete();
        },
      });
    });
  }
};
