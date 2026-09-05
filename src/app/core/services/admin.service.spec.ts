import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../../environments/environment';
import { AdminSearch } from '../interfaces/admin-search';
import { AdminStats } from '../interfaces/admin-stats';
import { DeletedAccount } from '../interfaces/deleted-account';
import { userFixture } from '../testing/user.fixture';
import { AdminService } from './admin.service';

describe('AdminService', () => {
  const apiUrl = environment.apiBaseUrl;
  const search: AdminSearch = { email: 'bob', orderBy: 'totalLikes' };
  let service: AdminService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AdminService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpController.verify());

  it('reads the statistics of the dashboard', () => {
    const stats: AdminStats = {
      totalUsers: 10,
      totalDeletedAccounts: 2,
      totalRecentUsers: 3,
      totalRecentDeletedAccounts: 1,
    };
    let received: AdminStats | undefined;

    service.getAdminStats().subscribe((response) => (received = response));

    const request = httpController.expectOne(`${apiUrl}/admin/stats`);
    expect(request.request.method).toBe('GET');
    expect(request.request.withCredentials).toBeTrue();
    request.flush(stats);
    expect(received).toEqual(stats);
  });

  it('posts the search criteria to read a page of accounts', () => {
    const users = [userFixture()];
    let received: unknown;

    service
      .getAllUsers(search, 2)
      .subscribe((response) => (received = response));

    const request = httpController.expectOne(`${apiUrl}/admin/user/all/2`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(search);
    request.flush(users);
    expect(received).toEqual(users);
  });

  it('posts the search criteria to read a page of archived accounts', () => {
    const accounts: DeletedAccount[] = [
      {
        nickname: 'nickname',
        email: 'user@pickme.com',
        deletionDate: new Date(),
        totalDislikes: 0,
        totalLikes: 0,
        totalMatches: 0,
        deletedBy: 'User',
      },
    ];
    let received: DeletedAccount[] | undefined;

    service
      .getAllDeletedAccounts(search, 0)
      .subscribe((response) => (received = response));

    const request = httpController.expectOne(
      `${apiUrl}/admin/deleted-account/all/0`,
    );
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(search);
    request.flush(accounts);
    expect(received).toEqual(accounts);
  });

  it('deletes the account of a user', () => {
    service.deleteUser(7).subscribe();

    const request = httpController.expectOne(`${apiUrl}/admin/7`);
    expect(request.request.method).toBe('DELETE');
    expect(request.request.withCredentials).toBeTrue();
    request.flush(null);
  });
});
