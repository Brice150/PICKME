import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Subject, of } from 'rxjs';
import { Notification } from '../core/interfaces/notification';
import { ConnectService } from '../core/services/connect.service';
import { NotificationService } from '../core/services/notification.service';
import { NavComponent } from './nav.component';

describe('NavComponent', () => {
  let fixture: ComponentFixture<NavComponent>;
  let component: NavComponent;
  let connectService: jasmine.SpyObj<ConnectService>;
  let notificationService: jasmine.SpyObj<NotificationService>;
  let toastr: jasmine.SpyObj<ToastrService>;
  let router: jasmine.SpyObj<Router>;
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

  /** Simulates the arrival of a batch of notifications from the polling. */
  function poll(notifications: Notification[], url = '/select'): void {
    notificationService.getAllUserNotifications.and.returnValue(
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
      logout: jasmine.createSpy('logout'),
      isAdmin: jasmine.createSpy('isAdmin').and.returnValue(false),
    } as unknown as jasmine.SpyObj<ConnectService>;
    serverEvents$ = new Subject<void>();
    notificationService = {
      serverEvents$,
      getAllUserNotifications: jasmine.createSpy('getAllUserNotifications'),
      markUserNotificationsAsSeen: jasmine.createSpy(
        'markUserNotificationsAsSeen',
      ),
    } as unknown as jasmine.SpyObj<NotificationService>;
    toastr = jasmine.createSpyObj<ToastrService>('ToastrService', ['success']);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    Object.defineProperty(router, 'url', {
      value: '/select',
      configurable: true,
    });
    notificationService.markUserNotificationsAsSeen.and.returnValue(
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

  it('waits for the account to be loaded before polling the notifications', () => {
    expect(notificationService.getAllUserNotifications).not.toHaveBeenCalled();

    poll([notification(1, false)]);

    expect(component.notifications.length).toBe(1);
  });

  it('counts the notifications that have not been seen', () => {
    poll([
      notification(1, false),
      notification(2, true),
      notification(3, false),
    ]);

    expect(component.getUnseenNotificationsLength()).toBe(2);
  });

  it('marks the notifications as seen while the user reads the conversations', () => {
    poll([notification(1, false)], '/match');

    expect(notificationService.markUserNotificationsAsSeen).toHaveBeenCalled();
    expect(component.notifications[0].seen).toBeTrue();
  });

  it('leaves an unmatch unread even on the conversations screen', () => {
    poll([notification(1, false, 'unmatch')], '/match');

    expect(
      notificationService.markUserNotificationsAsSeen,
    ).not.toHaveBeenCalled();
    expect(component.notifications[0].seen).toBeFalse();
  });

  it('opens and closes the menu', () => {
    component.toggleMenu();
    expect(component.isMenuActive).toBeTrue();

    component.toggleMenu();
    expect(component.isMenuActive).toBeFalse();
  });

  it('marks the notifications as seen when their panel is closed', () => {
    poll([notification(1, false)]);
    component.toggleNotifications();

    component.toggleNotifications();

    expect(notificationService.markUserNotificationsAsSeen).toHaveBeenCalled();
    expect(component.notifications[0].seen).toBeTrue();
  });

  it('does not call the API when every notification has already been seen', () => {
    poll([notification(1, true)]);
    component.toggleNotifications();

    component.toggleNotifications();

    expect(
      notificationService.markUserNotificationsAsSeen,
    ).not.toHaveBeenCalled();
  });

  it('closes the notification panel when the menu is toggled', () => {
    poll([notification(1, true)]);
    component.toggleNotifications();

    component.toggleMenu();

    expect(component.isNotificationsActive).toBeFalse();
  });

  it('opens the conversations and closes the menu', () => {
    component.toggleMenu();

    component.goTo();

    expect(router.navigate).toHaveBeenCalledWith(['match']);
    expect(component.isMenuActive).toBeFalse();
  });

  it('logs the user out and closes the menu', () => {
    component.toggleMenu();

    component.logout();

    expect(connectService.logout).toHaveBeenCalled();
    expect(component.isMenuActive).toBeFalse();
    expect(toastr.success.calls.mostRecent().args[1]).toBe('Logged Out');
  });
});
