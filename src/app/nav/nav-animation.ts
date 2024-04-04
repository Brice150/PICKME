import { animate, style, transition, trigger } from '@angular/animations';

export const MenuAnimation = trigger('menuTrigger', [
  transition(':enter', [
    style({
      transform: 'translateX(100%)',
    }),
    animate('0.3s ease', style({ transform: 'translateX(0%)' })),
  ]),

  transition(':leave', [
    animate('0.3s ease', style({ transform: 'translateX(100%)' })),
  ]),
]);

export const NotificationLogoAnimation = trigger('notificationLogoTrigger', [
  transition(':enter', [
    style({
      transform: 'translateX(50vw)',
    }),
    animate('0.3s ease', style({ transform: 'translateX(0%)' })),
  ]),

  transition(':leave', [
    animate('0.3s ease', style({ transform: 'translateX(50vw)' })),
  ]),
]);

export const NavButtonAnimation = trigger('navButtonTrigger', [
  transition(':enter', [
    style({
      position: 'absolute',
      left: '100%',
    }),
    animate('0.5s ease', style({ left: '0%' })),
  ]),

  transition(':leave', [animate('0.5s ease', style({ left: '100%' }))]),
]);

export const NotificationAnimation = trigger('notificationTrigger', [
  transition(':enter', [
    style({
      position: 'absolute',
      left: '-100%',
    }),
    animate('0.5s ease', style({ left: '0%' })),
  ]),

  transition(':leave', [animate('0.5s ease', style({ left: '-100%' }))]),
]);
