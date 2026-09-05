import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Picture } from '../interfaces/picture';
import { User } from '../interfaces/user';

@Injectable({
  providedIn: 'root',
})
export class SelectService {
  private readonly http = inject(HttpClient);
  private readonly apiServerUrl = environment.apiBaseUrl;

  getAllSelectedUsers(page: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiServerUrl}/user/all/${page}`, {
      withCredentials: true,
    });
  }

  getUserPictures(userId: number): Observable<Picture[]> {
    return this.http.get<Picture[]>(
      `${this.apiServerUrl}/picture/user/${userId}`,
      {
        withCredentials: true,
      },
    );
  }

  addLike(userId: number): Observable<string> {
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
      },
    );
  }
}
