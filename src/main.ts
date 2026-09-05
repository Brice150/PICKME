import { bootstrapApplication } from '@angular/platform-browser';
import { register as registerSwiperElements } from 'swiper/element/bundle';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

// Declares the <swiper-container> custom elements the selection and the albums rely on. It belongs
// to the bootstrap rather than to the providers: it touches the browser, not the injector.
registerSwiperElements();

bootstrapApplication(AppComponent, appConfig).catch((err) =>
  console.error(err),
);
