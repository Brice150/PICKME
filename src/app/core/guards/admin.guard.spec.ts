import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { firstValueFrom, Observable, of, throwError } from 'rxjs';
import { createSpyObj, SpyObj } from '../../../testing/spy';
import { UserRole } from '../enums/user-role';
import { User } from '../interfaces/user';
import { ConnectService } from '../services/connect.service';
import { userFixture } from '../testing/user.fixture';
import { adminGuard } from './admin.guard';

describe('adminGuard', () => {
  const admin = userFixture({ userRole: UserRole.ROLE_ADMIN });
  let connectService: SpyObj<ConnectService>;
  let router: SpyObj<Router>;
  let selectionUrl: UrlTree;

  beforeEach(() => {
    selectionUrl = {} as UrlTree;
    connectService = createSpyObj<ConnectService>(
      ['getConnectedUser', 'isAdmin'],
      { connectedUser: signal<User | undefined>(undefined) },
    );
    router = createSpyObj<Router>(['createUrlTree']);
    router.createUrlTree.mockReturnValue(selectionUrl);
    TestBed.configureTestingModule({
      providers: [
        { provide: ConnectService, useValue: connectService },
        { provide: Router, useValue: router },
      ],
    });
  });

  /** Runs the guard in an injection context, the router passing no route to it. */
  function runGuard(): boolean | UrlTree | Observable<boolean | UrlTree> {
    return TestBed.runInInjectionContext(
      () =>
        adminGuard(null!, null!) as
          boolean | UrlTree | Observable<boolean | UrlTree>,
    );
  }

  function connect(user: User | undefined): void {
    connectService.connectedUser.set(user);
  }

  it('lets an administrator already loaded through', () => {
    connect(admin);
    connectService.isAdmin.mockReturnValue(true);

    expect(runGuard()).toBe(true);
  });

  it('sends a standard user already loaded back to the selection', () => {
    connect(userFixture());
    connectService.isAdmin.mockReturnValue(false);

    expect(runGuard()).toBe(selectionUrl);
    expect(router.createUrlTree).toHaveBeenCalledWith(['/select']);
  });

  it('loads the account before letting an administrator through', async () => {
    connect(undefined);
    connectService.getConnectedUser.mockReturnValue(of(admin));
    connectService.isAdmin.mockReturnValue(true);

    const result = await firstValueFrom(
      runGuard() as Observable<boolean | UrlTree>,
    );

    expect(result).toBe(true);
  });

  it('sends a freshly loaded standard user back to the selection', async () => {
    connect(undefined);
    connectService.getConnectedUser.mockReturnValue(of(userFixture()));
    connectService.isAdmin.mockReturnValue(false);

    const result = await firstValueFrom(
      runGuard() as Observable<boolean | UrlTree>,
    );

    expect(result).toBe(selectionUrl);
  });

  it('sends a visitor without a session back to the selection', async () => {
    connect(undefined);
    connectService.getConnectedUser.mockReturnValue(
      throwError(() => new Error('unauthorized')),
    );

    const result = await firstValueFrom(
      runGuard() as Observable<boolean | UrlTree>,
    );

    expect(result).toBe(selectionUrl);
  });
});
