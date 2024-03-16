import { animate, style, transition, trigger } from '@angular/animations';

export const NavAnimation = trigger('menuTrigger', [
  transition(':enter', [
    style({
      transform: 'translateX(100%)',
    }),
    animate('0.5s ease', style({ transform: 'translateX(0%)' })),
  ]),

  transition(':leave', [
    animate('0.5s ease', style({ transform: 'translateX(100%)' })),
  ]),
]);
