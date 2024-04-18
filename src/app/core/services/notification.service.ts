import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Notification } from '../interfaces/notification';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  public getAllUserNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(
      `${this.apiServerUrl}/notification/all`,
      {
        withCredentials: true,
      }
    );
  }

  public markUserNotificationsAsSeen(): Observable<void> {
    return this.http.put<void>(`${this.apiServerUrl}/notification`, null, {
      withCredentials: true,
    });
  }
}
