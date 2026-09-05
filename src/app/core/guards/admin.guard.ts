import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { User } from '../interfaces/user';
import { ConnectService } from '../services/connect.service';

/**
 * Reserves a route to the administrators. When the account is not loaded yet, typically after a
 * page reload, it is fetched from the session still held by the API before the role is checked.
 */
export const adminGuard: CanActivateFn = () => {
  const connectService = inject(ConnectService);
  const router = inject(Router);
  const selection = (): UrlTree => router.createUrlTree(['/select']);

  if (connectService.connectedUser) {
    return connectService.isAdmin() || selection();
  }
  return connectService.getConnectedUser().pipe(
    map((user: User) => connectService.isAdmin(user) || selection()),
    catchError(() => of(selection())),
  );
};
