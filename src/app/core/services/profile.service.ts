import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Picture } from '../interfaces/picture';
import { User } from '../interfaces/user';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  http = inject(HttpClient);

  apiServerUrl = environment.apiBaseUrl;

  addPicture(pictureContent: string): Observable<Picture> {
    return this.http.post<Picture>(
      `${this.apiServerUrl}/picture`,
      pictureContent,
      {
        withCredentials: true,
      }
    );
  }

  selectMainPicture(pictureId: number): Observable<void> {
    return this.http.put<void>(
      `${this.apiServerUrl}/picture/${pictureId}`,
      null,
      {
        withCredentials: true,
      }
    );
  }

  deletePicture(pictureId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiServerUrl}/picture/${pictureId}`, {
      withCredentials: true,
    });
  }

  updateUser(user: User): Observable<User> {
    return this.http.put<User>(`${this.apiServerUrl}/user`, user, {
      withCredentials: true,
    });
  }

  deleteConnectedUser(): Observable<void> {
    return this.http.delete<void>(`${this.apiServerUrl}/user`, {
      withCredentials: true,
    });
  }
}
