import { HttpErrorResponse } from '@angular/common/http';
import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { of, throwError } from 'rxjs';
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
  let connectService: jasmine.SpyObj<ConnectService>;
  let router: jasmine.SpyObj<Router>;
  let toastr: jasmine.SpyObj<ToastrService>;

  /** Fills the form with valid credentials. */
  function fillForm(): void {
    component.loginForm.patchValue(credentials);
  }

  beforeEach(async () => {
    connectService = jasmine.createSpyObj<ConnectService>('ConnectService', [
      'login',
    ]);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    toastr = jasmine.createSpyObj<ToastrService>('ToastrService', [
      'success',
      'error',
    ]);
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
    expect(component.hide).toBeTrue();
  });

  it('requires a well formed email', () => {
    component.loginForm.get('email')?.setValue('not-an-email');

    expect(component.loginForm.get('email')?.valid).toBeFalse();
  });

  it('requires a password of at least five characters', () => {
    component.loginForm.get('password')?.setValue('abc');

    expect(component.loginForm.get('password')?.valid).toBeFalse();
  });

  it('does not call the API on an invalid form', () => {
    component.loginUser({} as User);

    expect(connectService.login).not.toHaveBeenCalled();
  });

  it('opens the selection screen once the user is logged in', () => {
    fillForm();
    connectService.login.and.returnValue(of(userFixture()));

    component.loginUser(credentials);

    expect(connectService.login).toHaveBeenCalledWith(credentials);
    expect(router.navigate).toHaveBeenCalledWith(['/select']);
    expect(component.loading).toBeFalse();
    expect(toastr.success.calls.mostRecent().args[1]).toBe('Logged In');
  });

  it('shakes the form on wrong credentials, then settles down', fakeAsync(() => {
    fillForm();
    connectService.login.and.returnValue(
      throwError(
        () => new HttpErrorResponse({ error: 'Bad credentials', status: 401 }),
      ),
    );

    component.loginUser(credentials);

    expect(component.invalidLogin).toBeTrue();
    expect(toastr.error.calls.mostRecent().args[1]).toBe('Bad Credentials');
    expect(router.navigate).not.toHaveBeenCalled();

    tick(2000);

    expect(component.invalidLogin).toBeFalse();
  }));

  it('reports any other failure without shaking the form', () => {
    fillForm();
    connectService.login.and.returnValue(
      throwError(
        () => new HttpErrorResponse({ error: { error: 'down' }, status: 500 }),
      ),
    );

    component.loginUser(credentials);

    expect(component.invalidLogin).toBeFalse();
    expect(toastr.error.calls.mostRecent().args[1]).toBe('Error');
    expect(component.loading).toBeFalse();
  });
});
