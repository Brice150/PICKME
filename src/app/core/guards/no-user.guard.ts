import { CanActivateFn, Router } from '@angular/router';
import { ConnectService } from '../services/connect.service';
import { inject } from '@angular/core';

export const noUserGuard: CanActivateFn = (route, state) => {
  const connectService = inject(ConnectService);
  const router = inject(Router);

  if (!connectService.connectedUser) {
    return true;
  } else {
    router.navigate(['/select']);
    return false;
  }
};
