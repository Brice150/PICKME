import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../interfaces/user';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SelectService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  public getAllSelectedUsers(page: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiServerUrl}/user/all/${page}`, {
      withCredentials: true,
    });
  }

  public addLike(userId: number): Observable<any> {
    return this.http.post<string>(`${this.apiServerUrl}/like/${userId}`, null, {
      withCredentials: true,
      responseType: 'text' as 'json',
    });
  }

  public addDislike(userId: number): Observable<void> {
    return this.http.post<void>(
      `${this.apiServerUrl}/dislike/${userId}`,
      null,
      {
        withCredentials: true,
      }
    );
  }
}
