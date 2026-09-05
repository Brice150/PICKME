import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { of } from 'rxjs';
import { ConnectService } from '../../core/services/connect.service';
import { userFixture } from '../../core/testing/user.fixture';
import { StartDemoComponent } from './start-demo.component';

describe('StartDemoComponent', () => {
  let fixture: ComponentFixture<StartDemoComponent>;
  let component: StartDemoComponent;
  let connectService: jasmine.SpyObj<ConnectService>;
  let router: jasmine.SpyObj<Router>;
  let toastr: jasmine.SpyObj<ToastrService>;

  beforeEach(async () => {
    connectService = jasmine.createSpyObj<ConnectService>('ConnectService', [
      'login',
    ]);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    toastr = jasmine.createSpyObj<ToastrService>('ToastrService', ['success']);
    await TestBed.configureTestingModule({
      imports: [StartDemoComponent],
      providers: [
        { provide: ConnectService, useValue: connectService },
        { provide: Router, useValue: router },
        { provide: ToastrService, useValue: toastr },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(StartDemoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('logs the freshly registered user in and opens their profile', () => {
    const registeredUser = userFixture({ password: 'password' });
    connectService.registeredUser = registeredUser;
    connectService.login.and.returnValue(of(registeredUser));

    component.startNow();

    expect(connectService.login).toHaveBeenCalledWith(registeredUser);
    expect(router.navigate).toHaveBeenCalledWith(['/profile']);
    expect(toastr.success.calls.mostRecent().args[1]).toBe('Logged In');
  });

  it('sends a visitor who did not register to the connection screen', () => {
    connectService.registeredUser = undefined;

    component.startNow();

    expect(connectService.login).not.toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
