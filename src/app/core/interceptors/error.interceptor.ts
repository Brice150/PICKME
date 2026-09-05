import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { catchError, throwError } from 'rxjs';
import { ConnectService } from '../services/connect.service';

const TOAST_OPTIONS = {
  positionClass: 'toast-bottom-center',
  toastClass: 'ngx-toastr custom error',
};

/**
 * Tells whether the failure means the account no longer exists: the menu polls the notifications
 * every ten seconds, so it is the first call to answer once an administrator closed the account.
 */
function isAccountGone(error: HttpErrorResponse): boolean {
  return (
    !!error.url?.includes('notification/all') &&
    (error.status === 404 || error.status === 500)
  );
}

/**
 * Reports the failed calls to the user.
 *
 * The session is only dropped when it is genuinely over: an expired session, or an account that
 * has been closed. A refused action or a server error leaves the user where they are, since
 * logging them out would cost them the screen they were on for a failure they can retry.
 *
 * The login call is left alone: the connection screen animates its own error.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastr = inject(ToastrService);
  const connectService = inject(ConnectService);

  if (req.url.includes('/login')) {
    return next(req);
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.url?.includes('/registration')) {
        toastr.error(error.error, 'Error', TOAST_OPTIONS);
      } else if (error.status === 401) {
        connectService.logout();
        toastr.error('Please login again', 'Unauthorized', TOAST_OPTIONS);
      } else if (isAccountGone(error)) {
        connectService.logout();
        toastr.error(
          'Your account has been deleted by an admin',
          'Account Deleted',
          TOAST_OPTIONS,
        );
      } else if (error.status === 403) {
        toastr.error(
          'You are not allowed to do this action',
          'Forbidden',
          TOAST_OPTIONS,
        );
      } else {
        toastr.error(error.message, 'Error', TOAST_OPTIONS);
      }
      return throwError(() => error);
    }),
  );
};
