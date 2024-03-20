import { animate, style, transition, trigger } from '@angular/animations';

export const TextAnimation = trigger('textTrigger', [
  transition(':enter', [
    style({
      transform: 'translateY(100%)',
    }),
    animate('0.5s ease', style({ transform: 'translateY(0%)' })),
  ]),
]);

export const DislikeButtonAnimation = trigger('dislikeButtonTrigger', [
  transition(':enter', [
    style({
      transform: 'translateX(100%)',
    }),
    animate('0.5s ease', style({ transform: 'translateX(0%)' })),
  ]),
]);

export const LikeButtonAnimation = trigger('likeButtonTrigger', [
  transition(':enter', [
    style({
      transform: 'translateX(-100%)',
    }),
    animate('0.5s ease', style({ transform: 'translateX(0%)' })),
  ]),
]);

export const TextMatchAnimation = trigger('textMatchTrigger', [
  transition(':enter', [
    style({
      transform: 'translateY(300%)',
    }),
    animate('1s ease', style({ transform: 'translateY(0%)' })),
  ]),
]);

export const LogoMatchAnimation = trigger('logoMatchTrigger', [
  transition(':enter', [
    style({
      transform: 'translateY(-300%)',
    }),
    animate('1s ease', style({ transform: 'translateY(0%)' })),
  ]),
]);
