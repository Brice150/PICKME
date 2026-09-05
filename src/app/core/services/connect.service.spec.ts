import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { UserRole } from '../enums/user-role';
import { Geolocation } from '../interfaces/geolocation';
import { userFixture } from '../testing/user.fixture';
import { ConnectService } from './connect.service';

describe('ConnectService', () => {
  const apiUrl = environment.apiBaseUrl;
  let service: ConnectService;
  let httpController: HttpTestingController;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: router },
      ],
    });
    service = TestBed.inject(ConnectService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpController.verify());

  it('posts the registration form', () => {
    const user = userFixture();
    let received: string | undefined;

    service.register(user).subscribe((response) => (received = response));

    const request = httpController.expectOne(`${apiUrl}/registration`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(user);
    expect(request.request.responseType).toBe('text');
    request.flush('OK');
    expect(received).toBe('OK');
  });

  it('authenticates with a basic authorization header and stores the account', () => {
    const credentials = userFixture({ password: 'password' });
    const loggedInUser = userFixture();
    let ready = false;
    service.connectedUserReady$.subscribe(() => (ready = true));

    service.login(credentials).subscribe();

    const request = httpController.expectOne(`${apiUrl}/login`);
    expect(request.request.method).toBe('GET');
    expect(request.request.headers.get('Authorization')).toBe(
      'Basic ' + window.btoa('user@pickme.com:password'),
    );
    request.flush(loggedInUser);
    expect(service.connectedUser).toEqual(loggedInUser);
    expect(ready).toBeTrue();
  });

  it('restores the account still held by the session', () => {
    const loggedInUser = userFixture();

    service.getConnectedUser().subscribe();

    const request = httpController.expectOne(`${apiUrl}/user`);
    expect(request.request.method).toBe('GET');
    expect(request.request.withCredentials).toBeTrue();
    request.flush(loggedInUser);
    expect(service.connectedUser).toEqual(loggedInUser);
  });

  it('reads the geolocation from the public ip service', () => {
    const geolocation: Geolocation = {
      latitude: '48.8566',
      longitude: '2.3522',
      distanceSearch: 100,
    };
    let received: Geolocation | undefined;

    service.getGeolocation().subscribe((response) => (received = response));

    const request = httpController.expectOne('https://ipapi.co/json/');
    expect(request.request.method).toBe('GET');
    request.flush(geolocation);
    expect(received).toEqual(geolocation);
  });

  it('drops the account and goes back to the connection screen on logout', () => {
    service.connectedUser = userFixture();
    let loggedOut = false;
    service.loggedOut$.subscribe(() => (loggedOut = true));

    service.logout();

    expect(router.navigate).toHaveBeenCalledWith(['/']);
    expect(service.connectedUser).toBeUndefined();
    expect(loggedOut).toBeTrue();
  });

  it('recognises an administrator', () => {
    service.connectedUser = userFixture({ userRole: UserRole.ROLE_ADMIN });

    expect(service.isAdmin()).toBeTrue();
  });

  it('does not take a standard user for an administrator', () => {
    service.connectedUser = userFixture();

    expect(service.isAdmin()).toBeFalse();
  });

  it('does not take a missing account for an administrator', () => {
    expect(service.isAdmin()).toBeFalse();
    expect(
      service.isAdmin(userFixture({ userRole: UserRole.ROLE_ADMIN })),
    ).toBeTrue();
  });
});
