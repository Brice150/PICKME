import { NgModule } from '@angular/core';
import { ExtraOptions, RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './admin.component/admin.component';
import { AppComponent } from './app.component/app.component';
import { ConnectComponent } from './connect.component/connect.component';
import { LoginComponent } from './connect.component/login.component/login.component';
import { RegisterComponent } from './connect.component/register.component/register.component';
import { HeaderComponent } from './header.component/header.component';
import { NavComponent } from './header.component/nav.component/nav.component';
import { LikeComponent } from './like.component/like.component';
import { MatchComponent } from './match.component/match.component';
import { MessageComponent } from './match.component/message.component/message.component';
import { ProfileComponent } from './profile.component/profile.component';
import { SelectComponent } from './select.component/select.component';
const routes: Routes = [
  {path: 'connect', component: ConnectComponent},
  {path: 'logout', component: ConnectComponent},
  {path: 'profile', component: ProfileComponent},
  {path: 'select', component: SelectComponent},
  {path: 'like', component: LikeComponent},
  {path: 'match', component: MatchComponent},
  {path: 'admin', component: AdminComponent},
  {path: '', redirectTo: '/connect', pathMatch: 'full'},
];

const routerOptions: ExtraOptions = {
  useHash: false,
  anchorScrolling: 'enabled'
};

@NgModule({
  imports: [RouterModule.forRoot(routes, routerOptions)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
export const routingComponents = [
  AppComponent, 
  ConnectComponent,
  LoginComponent,
  RegisterComponent,
  ProfileComponent,
  HeaderComponent,
  SelectComponent,
  MessageComponent,
  LikeComponent,
  MatchComponent,
  NavComponent,
  AdminComponent]
