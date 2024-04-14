import { Routes } from '@angular/router';
import { ConnectComponent } from './connect/connect.component';
import { SelectComponent } from './select/select.component';
import { ProfileComponent } from './profile/profile.component';
import { MatchComponent } from './match/match.component';
import { AdminComponent } from './admin/admin.component';
import { userGuard } from './core/guards/user.guard';
import { adminGuard } from './core/guards/admin.guard';
import { noUserGuard } from './core/guards/no-user.guard';
import { DemoComponent } from './demo/demo.component';

export const routes: Routes = [
  { path: '', component: ConnectComponent, canActivate: [noUserGuard] },
  { path: 'demo', component: DemoComponent, canActivate: [noUserGuard] },
  { path: 'select', component: SelectComponent, canActivate: [userGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [userGuard] },
  { path: 'match', component: MatchComponent, canActivate: [userGuard] },
  { path: 'admin', component: AdminComponent, canActivate: [adminGuard] },
  { path: '**', redirectTo: 'select', pathMatch: 'full' },
];
