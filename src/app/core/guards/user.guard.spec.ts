import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { firstValueFrom, Observable, of, throwError } from 'rxjs';
import { createSpyObj, SpyObj } from '../../../testing/spy';
import { User } from '../interfaces/user';
import { ConnectService } from '../services/connect.service';
import { userFixture } from '../testing/user.fixture';
import { userGuard } from './user.guard';

describe('userGuard', () => {
  const connectionUrl = {} as UrlTree;
  let connectService: SpyObj<ConnectService>;
  let router: SpyObj<Router>;

  beforeEach(() => {
    connectService = createSpyObj<ConnectService>(['getConnectedUser'], {
      connectedUser: signal<User | undefined>(undefined),
    });
    router = createSpyObj<Router>(['createUrlTree']);
    router.createUrlTree.mockReturnValue(connectionUrl);
    TestBed.configureTestingModule({
      providers: [
        { provide: ConnectService, useValue: connectService },
        { provide: Router, useValue: router },
      ],
    });
  });

  function runGuard(): boolean | UrlTree | Observable<boolean | UrlTree> {
    return TestBed.runInInjectionContext(
      () =>
        userGuard(null!, null!) as
          boolean | UrlTree | Observable<boolean | UrlTree>,
    );
  }

  function connect(user: User | undefined): void {
    connectService.connectedUser.set(user);
  }

  it('lets an account already loaded through', () => {
    connect(userFixture());

    expect(runGuard()).toBe(true);
    expect(connectService.getConnectedUser).not.toHaveBeenCalled();
  });

  it('loads the account still held by the session before letting it through', async () => {
    connect(undefined);
    connectService.getConnectedUser.mockReturnValue(of(userFixture()));

    const result = await firstValueFrom(
      runGuard() as Observable<boolean | UrlTree>,
    );

    expect(result).toBe(true);
  });

  it('sends a visitor without a session back to the connection screen', async () => {
    connect(undefined);
    connectService.getConnectedUser.mockReturnValue(
      throwError(() => new Error('unauthorized')),
    );

    const result = await firstValueFrom(
      runGuard() as Observable<boolean | UrlTree>,
    );

    expect(result).toBe(connectionUrl);
    expect(router.createUrlTree).toHaveBeenCalledWith(['/']);
  });
});
