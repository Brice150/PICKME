import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { ConnectService } from '../services/connect.service';

/**
 * Keeps the connection and the demonstration screens out of reach of an account already logged in.
 */
export const noUserGuard: CanActivateFn = () => {
  const connectService = inject(ConnectService);
  const router = inject(Router);

  return !connectService.connectedUser() || router.createUrlTree(['/select']);
};
