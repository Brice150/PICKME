import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, share } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Notification } from '../interfaces/notification';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly http = inject(HttpClient);
  private readonly apiServerUrl = environment.apiBaseUrl;

  /**
   * Signals sent by the server when something changed for the connected user: a new match, a new
   * message, an account that closed.
   *
   * The event carries no payload on purpose. It only says that something happened, and the
   * screens read what they need back from the regular endpoints, so a single place keeps deciding
   * what a user is allowed to see.
   *
   * The connection is shared between the screens that listen to it, and closed once the last of
   * them unsubscribes. The browser reconnects on its own when it drops.
   */
  readonly serverEvents$: Observable<void> = new Observable<void>(
    (subscriber) => {
      const source = new EventSource(
        `${this.apiServerUrl}/notification/stream`,
        {
          withCredentials: true,
        },
      );
      source.addEventListener('notification', () => subscriber.next());
      return () => source.close();
    },
  ).pipe(share());

  getAllUserNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(
      `${this.apiServerUrl}/notification/all`,
      {
        withCredentials: true,
      },
    );
  }

  markUserNotificationsAsSeen(): Observable<void> {
    return this.http.put<void>(`${this.apiServerUrl}/notification`, null, {
      withCredentials: true,
    });
  }
}
