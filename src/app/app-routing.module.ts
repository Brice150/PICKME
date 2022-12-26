import { NgModule } from '@angular/core';
import { ExtraOptions, RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component/app.component';
import { ConnectComponent } from './connect.component/connect.component';
import { LoginComponent } from './connect.component/login.component/login.component';
import { RegisterComponent } from './connect.component/register.component/register.component';
import { ProfileComponent } from './profile.component/profile.component';
const routes: Routes = [
  {path: 'connect', component: ConnectComponent},
  {path: 'logout', component: ConnectComponent},
  {path: 'profile', component: ProfileComponent},
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
  ProfileComponent]
