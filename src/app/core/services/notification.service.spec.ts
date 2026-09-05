import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Subscription } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Notification } from '../interfaces/notification';
import { NotificationService } from './notification.service';

describe('NotificationService', () => {
  const apiUrl = environment.apiBaseUrl;
  let service: NotificationService;
  let httpController: HttpTestingController;

  /** Stands in for the stream the browser opens, so that a test can play the server events. */
  class FakeEventSource {
    static opened: FakeEventSource[] = [];
    readonly listeners = new Map<string, () => void>();
    closed = false;

    constructor(
      readonly url: string,
      readonly options?: EventSourceInit,
    ) {
      FakeEventSource.opened.push(this);
    }

    addEventListener(name: string, listener: () => void): void {
      this.listeners.set(name, listener);
    }

    close(): void {
      this.closed = true;
    }

    emit(name: string): void {
      this.listeners.get(name)?.();
    }
  }

  const realEventSource = window.EventSource;

  beforeEach(() => {
    FakeEventSource.opened = [];
    (window as unknown as { EventSource: unknown }).EventSource =
      FakeEventSource;
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(NotificationService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    (window as unknown as { EventSource: unknown }).EventSource =
      realEventSource;
    httpController.verify();
  });

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

  it('opens the stream with the session cookie', () => {
    const subscription = service.serverEvents$.subscribe();

    expect(FakeEventSource.opened.length).toBe(1);
    expect(FakeEventSource.opened[0].url).toBe(`${apiUrl}/notification/stream`);
    expect(FakeEventSource.opened[0].options?.withCredentials).toBeTrue();

    subscription.unsubscribe();
  });

  it('emits every time the server signals a change', () => {
    let signals = 0;
    const subscription = service.serverEvents$.subscribe(() => signals++);

    FakeEventSource.opened[0].emit('notification');
    FakeEventSource.opened[0].emit('notification');

    expect(signals).toBe(2);
    subscription.unsubscribe();
  });

  it('opens a single connection for every screen listening', () => {
    const subscriptions = new Subscription();
    subscriptions.add(service.serverEvents$.subscribe());
    subscriptions.add(service.serverEvents$.subscribe());

    expect(FakeEventSource.opened.length).toBe(1);

    subscriptions.unsubscribe();
  });

  it('closes the connection once the last screen stops listening', () => {
    const menu = service.serverEvents$.subscribe();
    const conversations = service.serverEvents$.subscribe();

    menu.unsubscribe();
    expect(FakeEventSource.opened[0].closed).toBeFalse();

    conversations.unsubscribe();
    expect(FakeEventSource.opened[0].closed).toBeTrue();
  });
});
