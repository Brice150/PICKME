import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { ConnectService } from '../services/connect.service';
import { ProfileService } from '../services/profile.service';
import { Observable } from 'rxjs';
import { User } from '../interfaces/user';

export const adminGuard: CanActivateFn = (route, state) => {
  const connectService = inject(ConnectService);
  const profileService = inject(ProfileService);
  const router = inject(Router);

  if (connectService.connectedUser?.userRole === 'ROLE_ADMIN') {
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
          router.navigate(['/select']);
          observer.next(false);
          observer.complete();
        },
      });
    });
  }
};
