import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { createSpyObj, SpyObj } from '../../../testing/spy';
import { User } from '../interfaces/user';
import { ConnectService } from '../services/connect.service';
import { userFixture } from '../testing/user.fixture';
import { noUserGuard } from './no-user.guard';

describe('noUserGuard', () => {
  const selectionUrl = {} as UrlTree;
  let connectService: SpyObj<ConnectService>;
  let router: SpyObj<Router>;

  beforeEach(() => {
    connectService = createSpyObj<ConnectService>([], {
      connectedUser: signal<User | undefined>(undefined),
    });
    router = createSpyObj<Router>(['createUrlTree']);
    router.createUrlTree.mockReturnValue(selectionUrl);
    TestBed.configureTestingModule({
      providers: [
        { provide: ConnectService, useValue: connectService },
        { provide: Router, useValue: router },
      ],
    });
  });

  function connect(user: User | undefined): void {
    connectService.connectedUser.set(user);
  }

  it('lets a visitor reach the connection screen', () => {
    connect(undefined);

    expect(TestBed.runInInjectionContext(() => noUserGuard(null!, null!))).toBe(
      true,
    );
  });

  it('sends a connected account to the selection screen', () => {
    connect(userFixture());

    expect(TestBed.runInInjectionContext(() => noUserGuard(null!, null!))).toBe(
      selectionUrl,
    );
    expect(router.createUrlTree).toHaveBeenCalledWith(['/select']);
  });
});
