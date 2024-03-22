import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastr = inject(ToastrService);
  if (!req.url.includes('/login')) {
    return next(req).pipe(
      catchError((error: HttpErrorResponse) => {
        toastr.error(error.message, 'Error', {
          positionClass: 'toast-bottom-center',
          toastClass: 'ngx-toastr custom error',
        });
        return throwError(() => error);
      })
    );
  } else {
    return next(req);
  }
};
