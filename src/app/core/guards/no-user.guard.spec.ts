import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { User } from '../interfaces/user';
import { ConnectService } from '../services/connect.service';
import { userFixture } from '../testing/user.fixture';
import { noUserGuard } from './no-user.guard';

describe('noUserGuard', () => {
  const selectionUrl = {} as UrlTree;
  let connectService: jasmine.SpyObj<ConnectService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    connectService = jasmine.createSpyObj<ConnectService>(
      'ConnectService',
      [],
      {
        connectedUser: undefined,
      },
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

  function connect(user: User | undefined): void {
    Object.defineProperty(connectService, 'connectedUser', {
      value: user,
      configurable: true,
    });
  }

  it('lets a visitor reach the connection screen', () => {
    connect(undefined);

    expect(
      TestBed.runInInjectionContext(() => noUserGuard(null!, null!)),
    ).toBeTrue();
  });

  it('sends a connected account to the selection screen', () => {
    connect(userFixture());

    expect(TestBed.runInInjectionContext(() => noUserGuard(null!, null!))).toBe(
      selectionUrl,
    );
    expect(router.createUrlTree).toHaveBeenCalledWith(['/select']);
  });
});
