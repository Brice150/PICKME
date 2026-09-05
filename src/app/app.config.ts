import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { provideAnimations } from '@angular/platform-browser/animations';
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
    // Angular 21 runs zoneless by default. The screens hold their state in plain fields that they
    // mutate from their subscriptions, which notifies nothing: without zone.js driving the change
    // detection, a response would only reach the view on the next DOM event. Migrating that state
    // to signals would let this line go.
    provideZoneChangeDetection(),
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
    provideAnimations(),
    provideAnimationsAsync(),
    { provide: MAT_DATE_LOCALE, useValue: 'en-GB' },
  ],
};
