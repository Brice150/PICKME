import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { User } from '../interfaces/user';
import { Observable, Subject, of, switchMap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Geolocation } from '../interfaces/geolocation';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class ConnectService {
  http = inject(HttpClient);
  router = inject(Router);

  apiServerUrl = environment.apiBaseUrl;
  registeredUser?: User;
  connectedUser?: User;
  connectedUserReady$: Subject<void> = new Subject<void>();
  loggedOut$: Subject<void> = new Subject<void>();

  register(user: User): any {
    return this.http.post(`${this.apiServerUrl}/registration`, user, {
      withCredentials: true,
      responseType: 'text',
    });
  }

  login(user: User): Observable<User> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + window.btoa(user.email + ':' + user.password),
    });
    return this.http
      .get<User>(`${this.apiServerUrl}/login`, {
        withCredentials: true,
        headers,
      })
      .pipe(
        switchMap((loggedInUser: User) => {
          this.connectedUser = loggedInUser;
          this.connectedUserReady$.next();
          return of(loggedInUser);
        })
      );
  }

  getConnectedUser(): Observable<User> {
    return this.http
      .get<User>(`${this.apiServerUrl}/user`, {
        withCredentials: true,
      })
      .pipe(
        switchMap((loggedInUser: User) => {
          this.connectedUser = loggedInUser;
          this.connectedUserReady$.next();
          return of(loggedInUser);
        })
      );
  }

  getGeolocation(): Observable<Geolocation> {
    return this.http.get<Geolocation>('https://ipapi.co/json/');
  }

  logout() {
    this.router.navigate(['/']);
    this.connectedUser = undefined;
    this.loggedOut$.next();
  }
}
