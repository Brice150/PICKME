import {
  HttpClient,
  HttpErrorResponse,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ToastrService } from 'ngx-toastr';
import { environment } from '../../../environments/environment';
import { createSpyObj, SpyObj } from '../../../testing/spy';
import { ConnectService } from '../services/connect.service';
import { errorInterceptor } from './error.interceptor';

describe('errorInterceptor', () => {
  const apiUrl = environment.apiBaseUrl;
  let http: HttpClient;
  let httpController: HttpTestingController;
  let toastr: SpyObj<ToastrService>;
  let connectService: SpyObj<ConnectService>;

  beforeEach(() => {
    toastr = createSpyObj<ToastrService>(['error']);
    connectService = createSpyObj<ConnectService>(['logout']);
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
        { provide: ToastrService, useValue: toastr },
        { provide: ConnectService, useValue: connectService },
      ],
    });
    http = TestBed.inject(HttpClient);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpController.verify());

  /**
   * Sends a request the interceptor sees, then makes it fail.
   *
   * @param url    url of the request
   * @param status status of the response
   * @param body   body of the response
   * @return the error the caller receives
   */
  function failingRequest(
    url: string,
    status: number,
    body = 'body',
  ): HttpErrorResponse {
    let caught: HttpErrorResponse | undefined;
    http.get(url).subscribe({ error: (error) => (caught = error) });
    httpController.expectOne(url).flush(body, { status, statusText: 'error' });
    return caught as HttpErrorResponse;
  }

  /** Returns the title the toast has been opened with. */
  function toastTitle(): string {
    return toastr.error.mock.lastCall![1] as string;
  }

  /** Returns the message the toast has been opened with. */
  function toastMessage(): string {
    return toastr.error.mock.lastCall![0] as string;
  }

  it('leaves the login call alone so that the screen can animate its own error', () => {
    const error = failingRequest(`${apiUrl}/login`, 401);

    expect(error.status).toBe(401);
    expect(toastr.error).not.toHaveBeenCalled();
    expect(connectService.logout).not.toHaveBeenCalled();
  });

  it('shows the reason the registration was rejected without logging out', () => {
    failingRequest(`${apiUrl}/registration`, 400, 'Email already taken');

    expect(toastMessage()).toBe('Email already taken');
    expect(toastTitle()).toBe('Error');
    expect(connectService.logout).not.toHaveBeenCalled();
  });

  it('warns the user on a refused action but keeps them logged in', () => {
    failingRequest(`${apiUrl}/admin/stats`, 403);

    // The session is still valid: only that one action was refused.
    expect(connectService.logout).not.toHaveBeenCalled();
    expect(toastMessage()).toBe('You are not allowed to do this action');
    expect(toastTitle()).toBe('Forbidden');
  });

  it('logs out and asks the user to log in again on an expired session', () => {
    failingRequest(`${apiUrl}/user`, 401);

    expect(connectService.logout).toHaveBeenCalled();
    expect(toastMessage()).toBe('Please login again');
    expect(toastTitle()).toBe('Unauthorized');
  });

  it('tells the user their account has been deleted when the notifications vanish', () => {
    failingRequest(`${apiUrl}/notification/all`, 404);

    expect(toastMessage()).toBe('Your account has been deleted by an admin');
    expect(toastTitle()).toBe('Account Deleted');
  });

  it('also tells the user their account has been deleted on a server error', () => {
    failingRequest(`${apiUrl}/notification/all`, 500);

    expect(toastTitle()).toBe('Account Deleted');
  });

  it('reports any other failure without dropping the session', () => {
    const error = failingRequest(`${apiUrl}/match/all`, 502);

    // A server hiccup must not cost the user the screen they were on.
    expect(connectService.logout).not.toHaveBeenCalled();
    expect(toastMessage()).toBe(error.message);
    expect(toastTitle()).toBe('Error');
  });

  it('lets a successful response through untouched', () => {
    let received: unknown;
    http.get(`${apiUrl}/user`).subscribe((response) => (received = response));

    httpController.expectOne(`${apiUrl}/user`).flush({ id: 1 });

    expect(received).toEqual({ id: 1 });
    expect(toastr.error).not.toHaveBeenCalled();
  });
});
