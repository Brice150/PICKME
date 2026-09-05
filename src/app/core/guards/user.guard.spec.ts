import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { User } from '../interfaces/user';
import { ConnectService } from '../services/connect.service';
import { userFixture } from '../testing/user.fixture';
import { userGuard } from './user.guard';

describe('userGuard', () => {
  const connectionUrl = {} as UrlTree;
  let connectService: jasmine.SpyObj<ConnectService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    connectService = jasmine.createSpyObj<ConnectService>(
      'ConnectService',
      ['getConnectedUser'],
      { connectedUser: undefined },
    );
    router = jasmine.createSpyObj<Router>('Router', ['createUrlTree']);
    router.createUrlTree.and.returnValue(connectionUrl);
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
    Object.defineProperty(connectService, 'connectedUser', {
      value: user,
      configurable: true,
    });
  }

  it('lets an account already loaded through', () => {
    connect(userFixture());

    expect(runGuard()).toBeTrue();
    expect(connectService.getConnectedUser).not.toHaveBeenCalled();
  });

  it('loads the account still held by the session before letting it through', (done) => {
    connect(undefined);
    connectService.getConnectedUser.and.returnValue(of(userFixture()));

    (runGuard() as Observable<boolean | UrlTree>).subscribe((result) => {
      expect(result).toBeTrue();
      done();
    });
  });

  it('sends a visitor without a session back to the connection screen', (done) => {
    connect(undefined);
    connectService.getConnectedUser.and.returnValue(
      throwError(() => new Error('unauthorized')),
    );

    (runGuard() as Observable<boolean | UrlTree>).subscribe((result) => {
      expect(result).toBe(connectionUrl);
      expect(router.createUrlTree).toHaveBeenCalledWith(['/']);
      done();
    });
  });
});
