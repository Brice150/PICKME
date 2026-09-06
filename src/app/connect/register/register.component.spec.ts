import { HttpErrorResponse } from '@angular/common/http';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { of, throwError } from 'rxjs';
import { createSpyObj, SpyObj } from '../../../testing/spy';
import { Gender } from '../../core/enums/gender';
import { Geolocation } from '../../core/interfaces/geolocation';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';
import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  const birthDate = new Date(1995, 5, 15);
  let fixture: ComponentFixture<RegisterComponent>;
  let component: RegisterComponent;
  let connectService: SpyObj<ConnectService>;
  let router: SpyObj<Router>;
  let toastr: SpyObj<ToastrService>;

  /** Fills the three steps of the form with a valid registration. */
  function fillForm(): void {
    component.firstFormGroup.patchValue({
      nickname: 'Alice',
      job: 'Architecte',
      birthDate,
      distanceSearch: 50,
    });
    component.secondFormGroup.patchValue({
      gender: Gender.WOMAN,
      genderSearch: Gender.MAN,
      minAge: 25,
      maxAge: 40,
    });
    component.thirdFormGroup.patchValue({
      email: 'alice@pickme.com',
      password: 'password',
      passwordDuplicate: 'password',
    });
  }

  beforeEach(async () => {
    connectService = {
      registeredUser: signal<User | undefined>(undefined),
      getGeolocation: vi.fn().mockReturnValue(
        of({
          latitude: '48.8566',
          longitude: '2.3522',
          city: 'Rouen',
          country_capital: 'Paris',
        } as Geolocation),
      ),
      register: vi.fn(),
    } as unknown as SpyObj<ConnectService>;
    router = createSpyObj<Router>(['navigate']);
    toastr = createSpyObj<ToastrService>(['success', 'error']);
    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        provideNoopAnimations(),
        { provide: ConnectService, useValue: connectService },
        { provide: Router, useValue: router },
        { provide: ToastrService, useValue: toastr },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('only accepts users who are at least eighteen', () => {
    const eighteenYearsAgo = new Date();

    expect(component.minDate.getFullYear()).toBe(
      eighteenYearsAgo.getFullYear() - 18,
    );
  });

  it('locates the user from their ip address', () => {
    expect(component.geolocation.latitude).toBe('48.8566');
    expect(component.geolocation.longitude).toBe('2.3522');
  });

  it('rejects an incomplete registration', () => {
    expect(component.registerForm.valid).toBe(false);

    component.registerUser();

    expect(connectService.register).not.toHaveBeenCalled();
  });

  it('rejects two passwords that do not match', () => {
    fillForm();
    component.thirdFormGroup.patchValue({ passwordDuplicate: 'different' });

    expect(
      component.thirdFormGroup
        .get('passwordDuplicate')
        ?.hasError('passwordMismatch'),
    ).toBe(true);
  });

  it('gathers the three steps into a single account', () => {
    fillForm();

    const user = component.setUser();

    expect(user).toEqual({
      nickname: 'Alice',
      job: 'Architecte',
      birthDate,
      genderAge: {
        gender: Gender.WOMAN,
        genderSearch: Gender.MAN,
        minAge: 25,
        maxAge: 40,
      },
      email: 'alice@pickme.com',
      password: 'password',
      geolocation: {
        latitude: '48.8566',
        longitude: '2.3522',
        distanceSearch: 50,
      },
    });
  });

  it('opens the demonstration once the account is created', () => {
    fillForm();
    connectService.register.mockReturnValue(of('OK'));

    component.registerUser();

    expect(connectService.register).toHaveBeenCalled();
    expect(connectService.registeredUser()?.email).toBe('alice@pickme.com');
    expect(router.navigate).toHaveBeenCalledWith(['/demo']);
    expect(component.loading()).toBe(false);
    expect(toastr.success.mock.lastCall![1]).toBe('Registration Successful');
  });

  it('keeps the user on the form when the registration is refused', () => {
    fillForm();
    connectService.register.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({ error: 'Email already taken', status: 403 }),
      ),
    );

    component.registerUser();

    expect(router.navigate).not.toHaveBeenCalled();
    expect(component.loading()).toBe(false);
    expect(toastr.error.mock.lastCall![1]).toBe('Error');
  });
});
