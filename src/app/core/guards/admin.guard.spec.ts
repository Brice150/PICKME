import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { UserRole } from '../enums/user-role';
import { User } from '../interfaces/user';
import { ConnectService } from '../services/connect.service';
import { userFixture } from '../testing/user.fixture';
import { adminGuard } from './admin.guard';

describe('adminGuard', () => {
  const admin = userFixture({ userRole: UserRole.ROLE_ADMIN });
  let connectService: jasmine.SpyObj<ConnectService>;
  let router: jasmine.SpyObj<Router>;
  let selectionUrl: UrlTree;

  beforeEach(() => {
    selectionUrl = {} as UrlTree;
    connectService = jasmine.createSpyObj<ConnectService>(
      'ConnectService',
      ['getConnectedUser', 'isAdmin'],
      { connectedUser: undefined },
    );
    router = jasmine.createSpyObj<Router>('Router', ['createUrlTree']);
    router.createUrlTree.and.returnValue(selectionUrl);
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
    Object.defineProperty(connectService, 'connectedUser', {
      value: user,
      configurable: true,
    });
  }

  it('lets an administrator already loaded through', () => {
    connect(admin);
    connectService.isAdmin.and.returnValue(true);

    expect(runGuard()).toBeTrue();
  });

  it('sends a standard user already loaded back to the selection', () => {
    connect(userFixture());
    connectService.isAdmin.and.returnValue(false);

    expect(runGuard()).toBe(selectionUrl);
    expect(router.createUrlTree).toHaveBeenCalledWith(['/select']);
  });

  it('loads the account before letting an administrator through', (done) => {
    connect(undefined);
    connectService.getConnectedUser.and.returnValue(of(admin));
    connectService.isAdmin.and.returnValue(true);

    (runGuard() as Observable<boolean | UrlTree>).subscribe((result) => {
      expect(result).toBeTrue();
      done();
    });
  });

  it('sends a freshly loaded standard user back to the selection', (done) => {
    connect(undefined);
    connectService.getConnectedUser.and.returnValue(of(userFixture()));
    connectService.isAdmin.and.returnValue(false);

    (runGuard() as Observable<boolean | UrlTree>).subscribe((result) => {
      expect(result).toBe(selectionUrl);
      done();
    });
  });

  it('sends a visitor without a session back to the selection', (done) => {
    connect(undefined);
    connectService.getConnectedUser.and.returnValue(
      throwError(() => new Error('unauthorized')),
    );

    (runGuard() as Observable<boolean | UrlTree>).subscribe((result) => {
      expect(result).toBe(selectionUrl);
      done();
    });
  });
});
