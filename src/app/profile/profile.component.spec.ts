import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ToastrService } from 'ngx-toastr';
import { of } from 'rxjs';
import { createSpyObj, SpyObj } from '../../testing/spy';
import { Geolocation } from '../core/interfaces/geolocation';
import { User } from '../core/interfaces/user';
import { ConnectService } from '../core/services/connect.service';
import { ProfileService } from '../core/services/profile.service';
import { userFixture } from '../core/testing/user.fixture';
import { ProfileComponent } from './profile.component';

describe('ProfileComponent', () => {
  let fixture: ComponentFixture<ProfileComponent>;
  let component: ProfileComponent;
  let connectService: SpyObj<ConnectService>;
  let profileService: SpyObj<ProfileService>;
  let toastr: SpyObj<ToastrService>;
  let connectedUser: User;

  /** Makes the ip lookup answer with a position. */
  function locateAt(geolocation: Partial<Geolocation>): void {
    connectService.getGeolocation.mockReturnValue(
      of({
        latitude: '48.8566',
        longitude: '2.3522',
        distanceSearch: 100,
        city: 'Rouen',
        country_capital: 'Paris',
        ...geolocation,
      } as Geolocation),
    );
  }

  function lastToastTitle(): string {
    return toastr.success.mock.lastCall![1] as string;
  }

  beforeEach(async () => {
    connectedUser = userFixture();
    connectService = {
      connectedUser: signal<User | undefined>(connectedUser),
      getGeolocation: vi.fn(),
      getConnectedUser: vi.fn(),
      logout: vi.fn(),
    } as unknown as SpyObj<ConnectService>;
    profileService = createSpyObj<ProfileService>([
      'updateUser',
      'deleteConnectedUser',
    ]);
    toastr = createSpyObj<ToastrService>(['success']);
    locateAt({});
    await TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [
        provideNoopAnimations(),
        { provide: ConnectService, useValue: connectService },
        { provide: ProfileService, useValue: profileService },
        { provide: ToastrService, useValue: toastr },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
  });

  it('edits a copy of the connected account, not the account itself', () => {
    fixture.detectChanges();

    expect(component.user()).toEqual(connectedUser);
    expect(component.user()).not.toBe(connectedUser);
  });

  it('locates the user from their ip address', () => {
    fixture.detectChanges();

    expect(component.geolocation.latitude).toBe('48.8566');
    expect(component.geolocation.longitude).toBe('2.3522');
  });

  it('asks the browser for a finer position when the ip only gives the capital', () => {
    vi.spyOn(navigator.geolocation, 'getCurrentPosition').mockImplementation(
      (success) => {
        success({
          coords: { latitude: 45.764, longitude: 4.8357 },
        } as GeolocationPosition);
      },
    );
    locateAt({ city: 'Paris', country_capital: 'Paris' });

    fixture.detectChanges();

    expect(navigator.geolocation.getCurrentPosition).toHaveBeenCalled();
    expect(component.geolocation.latitude).toBe('45.764');
  });

  it('saves the profile with the position and refreshes the connected account', () => {
    fixture.detectChanges();
    const updated = userFixture({ nickname: 'Alicia' });
    profileService.updateUser.mockReturnValue(of(updated));

    component.updateUser('Main Infos Updated');

    expect(component.user()!.geolocation.latitude).toBe('48.8566');
    expect(component.user()!.password).toBeUndefined();
    expect(connectService.connectedUser()).toBe(updated);
    expect(lastToastTitle()).toBe('Main Infos Updated');
  });

  it('announces the picture that has been promoted', () => {
    fixture.detectChanges();
    connectService.getConnectedUser.mockReturnValue(of(connectedUser));

    component.refreshUser('Main Picture Selected');

    expect(lastToastTitle()).toBe('Main Picture Selected');
  });

  it('announces a picture that has been added or deleted', () => {
    fixture.detectChanges();
    connectService.getConnectedUser.mockReturnValue(of(connectedUser));

    component.refreshUser('Picture Added');
    expect(lastToastTitle()).toBe('Picture Added');

    component.refreshUser('Picture Deleted');
    expect(lastToastTitle()).toBe('Picture Deleted');
  });

  it('refreshes silently when there is nothing to announce', () => {
    fixture.detectChanges();
    connectService.getConnectedUser.mockReturnValue(of(connectedUser));

    component.refreshUser('');

    expect(connectService.getConnectedUser).toHaveBeenCalled();
    expect(toastr.success).not.toHaveBeenCalled();
  });

  it('logs the user out once their account is deleted', () => {
    fixture.detectChanges();
    profileService.deleteConnectedUser.mockReturnValue(of(undefined));

    component.deleteAccount();

    expect(component.user()).toBeUndefined();
    expect(connectService.logout).toHaveBeenCalled();
    expect(lastToastTitle()).toBe('Account Deleted');
  });
});
