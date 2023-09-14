import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './admin/admin.component';
import { ConnectComponent } from './connect/connect.component';
import { LikeComponent } from './like/like.component';
import { MatchComponent } from './match/match.component';
import { MoreInfoComponent } from './moreinfo/moreinfo.component';
import { ProfileComponent } from './profile/profile.component';
import { SelectComponent } from './select/select.component';
import { ErrorPathComponent } from './error-path/error-path.component';
import { AuthGuard } from './core/auth.guard';

const routes: Routes = [
  { path: 'connect', component: ConnectComponent },
  { path: 'logout', component: ConnectComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'select', component: SelectComponent, canActivate: [AuthGuard] },
  { path: 'like', component: LikeComponent, canActivate: [AuthGuard] },
  { path: 'match', component: MatchComponent, canActivate: [AuthGuard] },
  { path: 'admin', component: AdminComponent, canActivate: [AuthGuard] },
  {
    path: 'moreinfo/:id/:mode',
    component: MoreInfoComponent,
    canActivate: [AuthGuard],
  },
  { path: '', redirectTo: '/connect', pathMatch: 'full' },
  { path: '**', component: ErrorPathComponent },
];

export const appRouter = RouterModule.forRoot(routes);
