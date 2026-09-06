import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Subject, of, throwError } from 'rxjs';
import { createSpyObj, SpyObj } from '../../testing/spy';
import { Notification } from '../core/interfaces/notification';
import { ConnectService } from '../core/services/connect.service';
import { NotificationService } from '../core/services/notification.service';
import { NavComponent } from './nav.component';

describe('NavComponent', () => {
  let fixture: ComponentFixture<NavComponent>;
  let component: NavComponent;
  let connectService: SpyObj<ConnectService>;
  let notificationService: SpyObj<NotificationService>;
  let toastr: SpyObj<ToastrService>;
  let router: SpyObj<Router>;
  let connectedUserReady$: Subject<void>;
  let serverEvents$: Subject<void>;
  let loggedOut$: Subject<void>;

  function notification(
    id: number,
    seen: boolean,
    link = 'match',
  ): Notification {
    return { id, content: 'content', link, date: new Date(), seen };
  }

  /** Simulates a batch of notifications arriving after a signal from the server. */
  function receive(notifications: Notification[], url = '/select'): void {
    notificationService.getAllUserNotifications.mockReturnValue(
      of(notifications),
    );
    Object.defineProperty(router, 'url', { value: url, configurable: true });
    connectedUserReady$.next();
  }

  beforeEach(async () => {
    connectedUserReady$ = new Subject<void>();
    loggedOut$ = new Subject<void>();
    connectService = {
      connectedUserReady$,
      loggedOut$,
      logout: vi.fn(),
      isAdmin: vi.fn().mockReturnValue(false),
    } as unknown as SpyObj<ConnectService>;
    serverEvents$ = new Subject<void>();
    notificationService = {
      serverEvents$,
      getAllUserNotifications: vi.fn(),
      markUserNotificationsAsSeen: vi.fn(),
    } as unknown as SpyObj<NotificationService>;
    toastr = createSpyObj<ToastrService>(['success']);
    router = createSpyObj<Router>(['navigate']);
    Object.defineProperty(router, 'url', {
      value: '/select',
      configurable: true,
    });
    notificationService.markUserNotificationsAsSeen.mockReturnValue(
      of(undefined),
    );
    await TestBed.configureTestingModule({
      imports: [NavComponent],
      providers: [
        provideNoopAnimations(),
        { provide: ConnectService, useValue: connectService },
        { provide: NotificationService, useValue: notificationService },
        { provide: ToastrService, useValue: toastr },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(NavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('waits for the account to be loaded before reading the notifications', () => {
    expect(notificationService.getAllUserNotifications).not.toHaveBeenCalled();

    receive([notification(1, false)]);

    expect(component.notifications.length).toBe(1);
  });

  it('keeps following the stream after a read has failed', () => {
    notificationService.getAllUserNotifications.mockReturnValue(
      throwError(() => new Error('offline')),
    );
    connectedUserReady$.next();

    notificationService.getAllUserNotifications.mockReturnValue(
      of([notification(1, false)]),
    );
    serverEvents$.next();

    expect(component.notifications.length).toBe(1);
  });

  it('counts the notifications that have not been seen', () => {
    receive([
      notification(1, false),
      notification(2, true),
      notification(3, false),
    ]);

    expect(component.getUnseenNotificationsLength()).toBe(2);
  });

  it('marks the notifications as seen while the user reads the conversations', () => {
    receive([notification(1, false)], '/match');

    expect(notificationService.markUserNotificationsAsSeen).toHaveBeenCalled();
    expect(component.notifications[0].seen).toBe(true);
  });

  it('leaves an unmatch unread even on the conversations screen', () => {
    receive([notification(1, false, 'unmatch')], '/match');

    expect(
      notificationService.markUserNotificationsAsSeen,
    ).not.toHaveBeenCalled();
    expect(component.notifications[0].seen).toBe(false);
  });

  it('opens and closes the menu', () => {
    component.toggleMenu();
    expect(component.isMenuActive).toBe(true);

    component.toggleMenu();
    expect(component.isMenuActive).toBe(false);
  });

  it('marks the notifications as seen when their panel is closed', () => {
    receive([notification(1, false)]);
    component.toggleNotifications();

    component.toggleNotifications();

    expect(notificationService.markUserNotificationsAsSeen).toHaveBeenCalled();
    expect(component.notifications[0].seen).toBe(true);
  });

  it('does not call the API when every notification has already been seen', () => {
    receive([notification(1, true)]);
    component.toggleNotifications();

    component.toggleNotifications();

    expect(
      notificationService.markUserNotificationsAsSeen,
    ).not.toHaveBeenCalled();
  });

  it('closes the notification panel when the menu is toggled', () => {
    receive([notification(1, true)]);
    component.toggleNotifications();

    component.toggleMenu();

    expect(component.isNotificationsActive).toBe(false);
  });

  it('opens the conversations and closes the menu', () => {
    component.toggleMenu();

    component.goTo();

    expect(router.navigate).toHaveBeenCalledWith(['match']);
    expect(component.isMenuActive).toBe(false);
  });

  it('logs the user out and closes the menu', () => {
    component.toggleMenu();

    component.logout();

    expect(connectService.logout).toHaveBeenCalled();
    expect(component.isMenuActive).toBe(false);
    expect(toastr.success.mock.lastCall![1]).toBe('Logged Out');
  });
});
