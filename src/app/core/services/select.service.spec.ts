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
import { SelectService } from './select.service';

describe('SelectService', () => {
  const apiUrl = environment.apiBaseUrl;
  let service: SelectService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(SelectService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpController.verify());

  it('reads a page of candidates', () => {
    const candidates = [userFixture({ id: 2 })];
    let received: User[] | undefined;

    service
      .getAllSelectedUsers(1)
      .subscribe((response) => (received = response));

    const request = httpController.expectOne(`${apiUrl}/user/all/1`);
    expect(request.request.method).toBe('GET');
    expect(request.request.withCredentials).toBeTrue();
    request.flush(candidates);
    expect(received).toEqual(candidates);
  });

  it('reads the album of a candidate on demand', () => {
    const pictures: Picture[] = [
      { id: 1, content: 'content', isMainPicture: true },
    ];
    let received: Picture[] | undefined;

    service.getUserPictures(2).subscribe((response) => (received = response));

    const request = httpController.expectOne(`${apiUrl}/picture/user/2`);
    expect(request.request.method).toBe('GET');
    request.flush(pictures);
    expect(received).toEqual(pictures);
  });

  it('sends a like and reads the match notification as text', () => {
    let received: string | undefined;

    service.addLike(2).subscribe((response) => (received = response));

    const request = httpController.expectOne(`${apiUrl}/like/2`);
    expect(request.request.method).toBe('POST');
    expect(request.request.responseType).toBe('text');
    request.flush('nickname');
    expect(received).toBe('nickname');
  });

  it('sends a dislike', () => {
    service.addDislike(2).subscribe();

    const request = httpController.expectOne(`${apiUrl}/dislike/2`);
    expect(request.request.method).toBe('POST');
    expect(request.request.withCredentials).toBeTrue();
    request.flush(null);
  });
});
