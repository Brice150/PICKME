import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { ConnectService } from '../services/connect.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const connectService = inject(ConnectService);
  const router = inject(Router);

  if (connectService.connectedUser?.userRole === 'ROLE_ADMIN') {
    return true;
  } else {
    router.navigate(['/select']);
    return false;
  }
};
