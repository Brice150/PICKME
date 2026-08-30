import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Notification } from '../interfaces/notification';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly http = inject(HttpClient);
  private readonly apiServerUrl = environment.apiBaseUrl;

  getAllUserNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(
      `${this.apiServerUrl}/notification/all`,
      {
        withCredentials: true,
      }
    );
  }

  markUserNotificationsAsSeen(): Observable<void> {
    return this.http.put<void>(`${this.apiServerUrl}/notification`, null, {
      withCredentials: true,
    });
  }
}
