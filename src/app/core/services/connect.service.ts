import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../interfaces/user';
import { Observable, of, switchMap } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ConnectService {
  private apiServerUrl = environment.apiBaseUrl;
  connectedUser?: User;

  constructor(private http: HttpClient) {}

  public register(user: User): any {
    return this.http.post(`${this.apiServerUrl}/registration`, user, {
      withCredentials: true,
      responseType: 'text',
    });
  }

  public login(user: User): Observable<User> {
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
          return of(loggedInUser);
        })
      );
  }

  public logout() {
    this.connectedUser = undefined;
  }
}
