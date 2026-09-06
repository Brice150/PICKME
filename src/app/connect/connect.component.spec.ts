import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { of } from 'rxjs';
import { createSpyObj } from '../../testing/spy';
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
          useValue: createSpyObj<Router>(['navigate']),
        },
        {
          provide: ToastrService,
          useValue: createSpyObj<ToastrService>(['success', 'error']),
        },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(ConnectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('opens on the login form', () => {
    expect(component.isRegistering()).toBe(false);
    expect(fixture.nativeElement.querySelector('app-login')).not.toBeNull();
  });

  it('switches to the registration form', () => {
    component.toggleLoginOrRegister('register');
    fixture.detectChanges();

    expect(component.isRegistering()).toBe(true);
    expect(fixture.nativeElement.querySelector('app-register')).not.toBeNull();
  });

  it('switches back to the login form', () => {
    component.toggleLoginOrRegister('register');

    component.toggleLoginOrRegister('login');

    expect(component.isRegistering()).toBe(false);
  });

  it('stays where it is when the form already displayed is asked for again', () => {
    component.toggleLoginOrRegister('login');

    expect(component.isRegistering()).toBe(false);
  });
});
