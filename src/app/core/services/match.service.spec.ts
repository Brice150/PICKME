import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../../environments/environment';
import { Match } from '../interfaces/match';
import { Message } from '../interfaces/message';
import { userFixture } from '../testing/user.fixture';
import { MatchService } from './match.service';

describe('MatchService', () => {
  const apiUrl = environment.apiBaseUrl;
  const message: Message = {
    id: 1,
    content: 'hello',
    date: new Date(),
    sender: 'nickname',
    fkReceiver: 2,
  };
  let service: MatchService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(MatchService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpController.verify());

  it('reads the matches with their conversation', () => {
    const matches: Match[] = [{ user: userFixture({ id: 2 }), messages: [] }];
    let received: Match[] | undefined;

    service.getAllUserMatches().subscribe((response) => (received = response));

    const request = httpController.expectOne(`${apiUrl}/match/all`);
    expect(request.request.method).toBe('GET');
    expect(request.request.withCredentials).toBe(true);
    request.flush(matches);
    expect(received).toEqual(matches);
  });

  it('sends a new message', () => {
    let received: Message | undefined;

    service.addMessage(message).subscribe((response) => (received = response));

    const request = httpController.expectOne(`${apiUrl}/message`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(message);
    request.flush(message);
    expect(received).toEqual(message);
  });

  it('edits an existing message', () => {
    service.updateMessage(message).subscribe();

    const request = httpController.expectOne(`${apiUrl}/message`);
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(message);
    request.flush(message);
  });

  it('deletes a message', () => {
    service.deleteMessage(1).subscribe();

    const request = httpController.expectOne(`${apiUrl}/message/1`);
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });
});
