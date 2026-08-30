import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, Subject, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Geolocation } from '../interfaces/geolocation';
import { User } from '../interfaces/user';

@Injectable({ providedIn: 'root' })
export class ConnectService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiServerUrl = environment.apiBaseUrl;

  registeredUser?: User;
  connectedUser?: User;
  readonly connectedUserReady$: Subject<void> = new Subject<void>();
  readonly loggedOut$: Subject<void> = new Subject<void>();

  register(user: User): Observable<string> {
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
      .pipe(tap((loggedInUser: User) => this.storeConnectedUser(loggedInUser)));
  }

  getConnectedUser(): Observable<User> {
    return this.http
      .get<User>(`${this.apiServerUrl}/user`, {
        withCredentials: true,
      })
      .pipe(tap((loggedInUser: User) => this.storeConnectedUser(loggedInUser)));
  }

  getGeolocation(): Observable<Geolocation> {
    return this.http.get<Geolocation>('https://ipapi.co/json/');
  }

  logout(): void {
    this.router.navigate(['/']);
    this.connectedUser = undefined;
    this.loggedOut$.next();
  }

  private storeConnectedUser(loggedInUser: User): void {
    this.connectedUser = loggedInUser;
    this.connectedUserReady$.next();
  }
}
