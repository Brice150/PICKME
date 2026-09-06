import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { of, throwError } from 'rxjs';
import { createSpyObj, SpyObj } from '../../../testing/spy';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';
import { userFixture } from '../../core/testing/user.fixture';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  const credentials = {
    email: 'user@pickme.com',
    password: 'password',
  } as User;
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;
  let connectService: SpyObj<ConnectService>;
  let router: SpyObj<Router>;
  let toastr: SpyObj<ToastrService>;

  /** Fills the form with valid credentials. */
  function fillForm(): void {
    component.loginForm.patchValue(credentials);
  }

  beforeEach(async () => {
    connectService = createSpyObj<ConnectService>(['login']);
    router = createSpyObj<Router>(['navigate']);
    toastr = createSpyObj<ToastrService>(['success', 'error']);
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideNoopAnimations(),
        { provide: ConnectService, useValue: connectService },
        { provide: Router, useValue: router },
        { provide: ToastrService, useValue: toastr },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('opens on an empty form, hiding the password', () => {
    expect(component.loginForm.value).toEqual({ email: '', password: '' });
    expect(component.hide).toBe(true);
  });

  it('requires a well formed email', () => {
    component.loginForm.get('email')?.setValue('not-an-email');

    expect(component.loginForm.get('email')?.valid).toBe(false);
  });

  it('requires a password of at least five characters', () => {
    component.loginForm.get('password')?.setValue('abc');

    expect(component.loginForm.get('password')?.valid).toBe(false);
  });

  it('does not call the API on an invalid form', () => {
    component.loginUser({} as User);

    expect(connectService.login).not.toHaveBeenCalled();
  });

  it('opens the selection screen once the user is logged in', () => {
    fillForm();
    connectService.login.mockReturnValue(of(userFixture()));

    component.loginUser(credentials);

    expect(connectService.login).toHaveBeenCalledWith(credentials);
    expect(router.navigate).toHaveBeenCalledWith(['/select']);
    expect(component.loading).toBe(false);
    expect(toastr.success.mock.lastCall![1]).toBe('Logged In');
  });

  it('shakes the form on wrong credentials, then settles down', () => {
    vi.useFakeTimers();
    fillForm();
    connectService.login.mockReturnValue(
      throwError(
        () => new HttpErrorResponse({ error: 'Bad credentials', status: 401 }),
      ),
    );

    component.loginUser(credentials);

    expect(component.invalidLogin).toBe(true);
    expect(toastr.error.mock.lastCall![1]).toBe('Bad Credentials');
    expect(router.navigate).not.toHaveBeenCalled();

    vi.advanceTimersByTime(2000);

    expect(component.invalidLogin).toBe(false);
  });

  it('reports any other failure without shaking the form', () => {
    fillForm();
    connectService.login.mockReturnValue(
      throwError(
        () => new HttpErrorResponse({ error: { error: 'down' }, status: 500 }),
      ),
    );

    component.loginUser(credentials);

    expect(component.invalidLogin).toBe(false);
    expect(toastr.error.mock.lastCall![1]).toBe('Error');
    expect(component.loading).toBe(false);
  });
});
