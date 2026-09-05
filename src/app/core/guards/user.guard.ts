import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { ConnectService } from '../services/connect.service';

/**
 * Reserves a route to the authenticated users. When the account is not loaded yet, typically after
 * a page reload, it is fetched from the session still held by the API.
 */
export const userGuard: CanActivateFn = () => {
  const connectService = inject(ConnectService);
  const router = inject(Router);
  const connection = (): UrlTree => router.createUrlTree(['/']);

  if (connectService.connectedUser) {
    return true;
  }
  return connectService.getConnectedUser().pipe(
    map(() => true as boolean | UrlTree),
    catchError(() => of(connection())),
  );
};
