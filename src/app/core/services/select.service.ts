import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { User } from '../interfaces/user';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SelectService {
  http = inject(HttpClient);

  apiServerUrl = environment.apiBaseUrl;

  getAllSelectedUsers(page: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiServerUrl}/user/all/${page}`, {
      withCredentials: true,
    });
  }

  addLike(userId: number): Observable<any> {
    return this.http.post<string>(`${this.apiServerUrl}/like/${userId}`, null, {
      withCredentials: true,
      responseType: 'text' as 'json',
    });
  }

  addDislike(userId: number): Observable<void> {
    return this.http.post<void>(
      `${this.apiServerUrl}/dislike/${userId}`,
      null,
      {
        withCredentials: true,
      }
    );
  }
}
