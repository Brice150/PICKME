import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { User } from '../interfaces/user';
import { Picture } from '../interfaces/picture';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private apiServerUrl = environment.apiBaseUrl;
  constructor(private http: HttpClient) {}

  public addPicture(pictureContent: string): Observable<Picture> {
    return this.http.post<Picture>(
      `${this.apiServerUrl}/picture`,
      pictureContent,
      {
        withCredentials: true,
      }
    );
  }

  public selectMainPicture(pictureId: number): Observable<void> {
    return this.http.put<void>(`${this.apiServerUrl}/picture/${pictureId}`, {
      withCredentials: true,
    });
  }

  public deletePicture(pictureId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiServerUrl}/picture/${pictureId}`, {
      withCredentials: true,
    });
  }

  public getConnectedUser(): Observable<User> {
    return this.http.get<User>(`${this.apiServerUrl}/user`, {
      withCredentials: true,
    });
  }

  public updateUser(user: User): Observable<User> {
    return this.http.put<User>(`${this.apiServerUrl}/user`, user, {
      withCredentials: true,
    });
  }

  public deleteConnectedUser(): Observable<void> {
    return this.http.delete<void>(`${this.apiServerUrl}/user`, {
      withCredentials: true,
    });
  }
}
