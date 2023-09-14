import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './admin/admin.component';
import { ConnectComponent } from './connect/connect.component';
import { LikeComponent } from './like/like.component';
import { MatchComponent } from './match/match.component';
import { MoreInfoComponent } from './moreinfo/moreinfo.component';
import { ProfileComponent } from './profile/profile.component';
import { SelectComponent } from './select/select.component';
import { ErrorPathComponent } from './error-path/error-path.component';

const routes: Routes = [
  { path: 'connect', component: ConnectComponent },
  { path: 'logout', component: ConnectComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'select', component: SelectComponent },
  { path: 'like', component: LikeComponent },
  { path: 'match', component: MatchComponent },
  { path: 'admin', component: AdminComponent },
  { path: 'moreinfo/:id/:mode', component: MoreInfoComponent },
  { path: '', redirectTo: '/connect', pathMatch: 'full' },
  { path: '**', component: ErrorPathComponent },
];

export const appRouter = RouterModule.forRoot(routes);
