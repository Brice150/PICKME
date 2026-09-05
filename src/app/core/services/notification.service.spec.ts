import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../../environments/environment';
import { Notification } from '../interfaces/notification';
import { NotificationService } from './notification.service';

describe('NotificationService', () => {
  const apiUrl = environment.apiBaseUrl;
  let service: NotificationService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(NotificationService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpController.verify());

  it('reads the notifications of the connected user', () => {
    const notifications: Notification[] = [
      {
        id: 1,
        content: 'New match with nickname',
        link: 'match',
        date: new Date(),
        seen: false,
      },
    ];
    let received: Notification[] | undefined;

    service
      .getAllUserNotifications()
      .subscribe((response) => (received = response));

    const request = httpController.expectOne(`${apiUrl}/notification/all`);
    expect(request.request.method).toBe('GET');
    expect(request.request.withCredentials).toBeTrue();
    request.flush(notifications);
    expect(received).toEqual(notifications);
  });

  it('marks the notifications as seen', () => {
    service.markUserNotificationsAsSeen().subscribe();

    const request = httpController.expectOne(`${apiUrl}/notification`);
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toBeNull();
    request.flush(null);
  });
});
