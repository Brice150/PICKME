import { provideHttpClient, withInterceptors } from '@angular/common/http';
import {
  ApplicationConfig,
  provideZonelessChangeDetection,
} from '@angular/core';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {
  provideRouter,
  withInMemoryScrolling,
  withPreloading,
} from '@angular/router';
import { provideToastr } from 'ngx-toastr';
import { routes } from './app.routes';
import { errorInterceptor } from './core/interceptors/error.interceptor';
import { SmartPreloading } from './core/services/smart-preloading.service';

export const appConfig: ApplicationConfig = {
  providers: [
    // The screens hold their state in signals and every component is checked on push: nothing
    // depends on zone.js noticing a task any more, so the change detection is driven by the
    // notifications the signals send.
    provideZonelessChangeDetection(),
    provideRouter(
      routes,
      withInMemoryScrolling({
        scrollPositionRestoration: 'top',
        anchorScrolling: 'enabled',
      }),
      withPreloading(SmartPreloading),
    ),
    provideHttpClient(withInterceptors([errorInterceptor])),
    provideToastr(),
    provideAnimationsAsync(),
    { provide: MAT_DATE_LOCALE, useValue: 'en-GB' },
  ],
};
