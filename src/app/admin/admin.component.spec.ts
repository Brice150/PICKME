import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ToastrService } from 'ngx-toastr';
import { of, throwError } from 'rxjs';
import { createSpyObj, SpyObj } from '../../testing/spy';
import { DeletedAccount } from '../core/interfaces/deleted-account';
import { User } from '../core/interfaces/user';
import { AdminService } from '../core/services/admin.service';
import { userFixture } from '../core/testing/user.fixture';
import { AdminComponent } from './admin.component';

describe('AdminComponent', () => {
  const archived: DeletedAccount = {
    nickname: 'Bob',
    email: 'bob@pickme.com',
    deletionDate: new Date(),
    totalDislikes: 0,
    totalLikes: 0,
    totalMatches: 0,
    deletedBy: 'User',
  };
  let fixture: ComponentFixture<AdminComponent>;
  let component: AdminComponent;
  let adminService: SpyObj<AdminService>;
  let toastr: SpyObj<ToastrService>;

  /** Starts the back office with a first page of accounts. */
  function start(users: User[] = [userFixture({ id: 2 })]): void {
    adminService.getAllUsers.mockReturnValue(of(users));
    fixture.detectChanges();
  }

  beforeEach(async () => {
    adminService = createSpyObj<AdminService>([
      'getAdminStats',
      'getAllUsers',
      'getAllDeletedAccounts',
      'deleteUser',
    ]);
    toastr = createSpyObj<ToastrService>(['success']);
    adminService.getAdminStats.mockReturnValue(
      of({
        totalUsers: 10,
        totalDeletedAccounts: 2,
        totalRecentUsers: 3,
        totalRecentDeletedAccounts: 1,
      }),
    );
    adminService.getAllUsers.mockReturnValue(of([]));
    adminService.getAllDeletedAccounts.mockReturnValue(of([archived]));
    await TestBed.configureTestingModule({
      imports: [AdminComponent],
      providers: [
        provideNoopAnimations(),
        { provide: AdminService, useValue: adminService },
        { provide: ToastrService, useValue: toastr },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(AdminComponent);
    component = fixture.componentInstance;
  });

  it('opens on the statistics and the first page of accounts', () => {
    start([userFixture({ id: 2 }), userFixture({ id: 3 })]);

    expect(component.adminStats()?.totalUsers).toBe(10);
    expect(component.users().length).toBe(2);
    expect(component.loading()).toBe(false);
    expect(component.searched()).toBe(true);
  });

  it('stops the loader when the accounts cannot be read', () => {
    adminService.getAllUsers.mockReturnValue(
      throwError(() => new Error('offline')),
    );

    fixture.detectChanges();

    expect(component.loading()).toBe(false);
  });

  it('searches the accounts on the criteria of the form', () => {
    start();
    component.adminForm.patchValue({ email: 'bob', orderBy: 'totalLikes' });

    component.search(0);

    expect(adminService.getAllUsers).toHaveBeenCalledWith(
      { email: 'bob', orderBy: 'totalLikes' },
      0,
    );
  });

  it('switches to the archived accounts', () => {
    start();

    component.toggleUserOrDeleted('deleted');

    expect(component.isUserMode()).toBe(false);
    expect(component.deletedAccounts()).toEqual([archived]);
    expect(adminService.getAllDeletedAccounts).toHaveBeenCalled();
  });

  it('stays where it is when the current tab is asked for again', () => {
    start();
    const searchesSoFar = adminService.getAllUsers.mock.calls.length;

    component.toggleUserOrDeleted('user');

    expect(component.isUserMode()).toBe(true);
    expect(adminService.getAllUsers.mock.calls.length).toBe(searchesSoFar);
  });

  it('follows the paginator', () => {
    start();

    component.handlePageEvent(2);

    expect(adminService.getAllUsers).toHaveBeenCalledWith(expect.anything(), 2);
  });

  it('searches again when the email field loses the focus after a search', () => {
    start();
    const searchesSoFar = adminService.getAllUsers.mock.calls.length;

    component.focusOut(0);

    expect(adminService.getAllUsers.mock.calls.length).toBe(searchesSoFar + 1);
  });

  it('does not search on an empty field that was never searched', () => {
    start();
    component.searched.set(false);
    const searchesSoFar = adminService.getAllUsers.mock.calls.length;

    component.focusOut(0);

    expect(adminService.getAllUsers.mock.calls.length).toBe(searchesSoFar);
  });

  it('removes a deleted account from the list', () => {
    const user = userFixture({ id: 2 });
    start([user, userFixture({ id: 3 })]);
    adminService.deleteUser.mockReturnValue(of(undefined));

    component.deleteUser(user);

    expect(component.users().map((u) => u.id)).toEqual([3]);
    expect(toastr.success.mock.lastCall![1]).toBe('User Deleted');
  });

  it('says nothing when the deleted account was already gone from the list', () => {
    start([userFixture({ id: 3 })]);
    adminService.deleteUser.mockReturnValue(of(undefined));

    component.deleteUser(userFixture({ id: 2 }));

    expect(component.users().length).toBe(1);
    expect(toastr.success).not.toHaveBeenCalled();
  });
});
