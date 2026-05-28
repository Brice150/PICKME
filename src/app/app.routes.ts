import { Routes } from '@angular/router';
import { userGuard } from './core/guards/user.guard';
import { adminGuard } from './core/guards/admin.guard';
import { noUserGuard } from './core/guards/no-user.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./connect/connect.component').then((m) => m.ConnectComponent),
    canActivate: [noUserGuard],
  },
  {
    path: 'demo',
    loadComponent: () =>
      import('./demo/demo.component').then((m) => m.DemoComponent),
    canActivate: [noUserGuard],
  },
  {
    path: 'select',
    loadComponent: () =>
      import('./select/select.component').then((m) => m.SelectComponent),
    canActivate: [userGuard],
  },
  {
    path: 'profile',
    loadComponent: () =>
      import('./profile/profile.component').then((m) => m.ProfileComponent),
    canActivate: [userGuard],
  },
  {
    path: 'match',
    loadComponent: () =>
      import('./match/match.component').then((m) => m.MatchComponent),
    canActivate: [userGuard],
  },
  {
    path: 'admin',
    loadComponent: () =>
      import('./admin/admin.component').then((m) => m.AdminComponent),
    canActivate: [adminGuard],
  },
  { path: '**', redirectTo: 'select', pathMatch: 'full' },
];
