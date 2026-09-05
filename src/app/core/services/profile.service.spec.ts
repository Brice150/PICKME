import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../../environments/environment';
import { Picture } from '../interfaces/picture';
import { User } from '../interfaces/user';
import { userFixture } from '../testing/user.fixture';
import { ProfileService } from './profile.service';

describe('ProfileService', () => {
  const apiUrl = environment.apiBaseUrl;
  let service: ProfileService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ProfileService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpController.verify());

  it('sends the content of a new picture', () => {
    const picture: Picture = { id: 1, content: 'base64', isMainPicture: true };
    let received: Picture | undefined;

    service.addPicture('base64').subscribe((response) => (received = response));

    const request = httpController.expectOne(`${apiUrl}/picture`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toBe('base64');
    request.flush(picture);
    expect(received).toEqual(picture);
  });

  it('promotes a picture as the main one', () => {
    service.selectMainPicture(3).subscribe();

    const request = httpController.expectOne(`${apiUrl}/picture/3`);
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toBeNull();
    request.flush(null);
  });

  it('deletes a picture', () => {
    service.deletePicture(3).subscribe();

    const request = httpController.expectOne(`${apiUrl}/picture/3`);
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });

  it('sends the updated profile', () => {
    const user = userFixture({ nickname: 'newNickname' });
    let received: User | undefined;

    service.updateUser(user).subscribe((response) => (received = response));

    const request = httpController.expectOne(`${apiUrl}/user`);
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(user);
    request.flush(user);
    expect(received).toEqual(user);
  });

  it('closes the account of the connected user', () => {
    service.deleteConnectedUser().subscribe();

    const request = httpController.expectOne(`${apiUrl}/user`);
    expect(request.request.method).toBe('DELETE');
    expect(request.request.withCredentials).toBeTrue();
    request.flush(null);
  });
});
