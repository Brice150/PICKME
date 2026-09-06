import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, WritableSignal, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, Subject, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserRole } from '../enums/user-role';
import { Geolocation } from '../interfaces/geolocation';
import { User } from '../interfaces/user';

@Injectable({ providedIn: 'root' })
export class ConnectService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiServerUrl = environment.apiBaseUrl;

  /**
   * The two accounts the application carries between screens. They are signals rather than plain
   * fields because the menu and the screens read them from their templates: a change has to notify
   * the change detection on its own, nothing else watches for it.
   */
  readonly registeredUser: WritableSignal<User | undefined> = signal<
    User | undefined
  >(undefined);
  readonly connectedUser: WritableSignal<User | undefined> = signal<
    User | undefined
  >(undefined);

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

  /**
   * Tells whether an account is an administrator, the connected one by default.
   *
   * @param user account to check
   */
  isAdmin(user: User | undefined = this.connectedUser()): boolean {
    return user?.userRole === UserRole.ROLE_ADMIN;
  }

  logout(): void {
    this.router.navigate(['/']);
    this.connectedUser.set(undefined);
    this.loggedOut$.next();
  }

  private storeConnectedUser(loggedInUser: User): void {
    this.connectedUser.set(loggedInUser);
    this.connectedUserReady$.next();
  }
}
