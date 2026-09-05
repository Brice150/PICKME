import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { of } from 'rxjs';
import { ConnectService } from '../core/services/connect.service';
import { ConnectComponent } from './connect.component';

describe('ConnectComponent', () => {
  let fixture: ComponentFixture<ConnectComponent>;
  let component: ConnectComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConnectComponent],
      providers: [
        provideNoopAnimations(),
        {
          provide: ConnectService,
          useValue: { getGeolocation: () => of({}) },
        },
        {
          provide: Router,
          useValue: jasmine.createSpyObj('Router', ['navigate']),
        },
        {
          provide: ToastrService,
          useValue: jasmine.createSpyObj('ToastrService', ['success', 'error']),
        },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(ConnectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('opens on the login form', () => {
    expect(component.isRegistering).toBeFalse();
    expect(fixture.nativeElement.querySelector('app-login')).not.toBeNull();
  });

  it('switches to the registration form', () => {
    component.toggleLoginOrRegister('register');
    fixture.detectChanges();

    expect(component.isRegistering).toBeTrue();
    expect(fixture.nativeElement.querySelector('app-register')).not.toBeNull();
  });

  it('switches back to the login form', () => {
    component.toggleLoginOrRegister('register');

    component.toggleLoginOrRegister('login');

    expect(component.isRegistering).toBeFalse();
  });

  it('stays where it is when the form already displayed is asked for again', () => {
    component.toggleLoginOrRegister('login');

    expect(component.isRegistering).toBeFalse();
  });
});
