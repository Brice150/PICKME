import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ToastrService } from 'ngx-toastr';
import { of } from 'rxjs';
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
  let adminService: jasmine.SpyObj<AdminService>;
  let toastr: jasmine.SpyObj<ToastrService>;

  /** Starts the back office with a first page of accounts. */
  function start(users: User[] = [userFixture({ id: 2 })]): void {
    adminService.getAllUsers.and.returnValue(of(users));
    fixture.detectChanges();
  }

  beforeEach(async () => {
    adminService = jasmine.createSpyObj<AdminService>('AdminService', [
      'getAdminStats',
      'getAllUsers',
      'getAllDeletedAccounts',
      'deleteUser',
    ]);
    toastr = jasmine.createSpyObj<ToastrService>('ToastrService', ['success']);
    adminService.getAdminStats.and.returnValue(
      of({
        totalUsers: 10,
        totalDeletedAccounts: 2,
        totalRecentUsers: 3,
        totalRecentDeletedAccounts: 1,
      }),
    );
    adminService.getAllUsers.and.returnValue(of([]));
    adminService.getAllDeletedAccounts.and.returnValue(of([archived]));
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

    expect(component.adminStats?.totalUsers).toBe(10);
    expect(component.users.length).toBe(2);
    expect(component.loading).toBeFalse();
    expect(component.searched).toBeTrue();
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

    expect(component.isUserMode).toBeFalse();
    expect(component.deletedAccounts).toEqual([archived]);
    expect(adminService.getAllDeletedAccounts).toHaveBeenCalled();
  });

  it('stays where it is when the current tab is asked for again', () => {
    start();
    const searchesSoFar = adminService.getAllUsers.calls.count();

    component.toggleUserOrDeleted('user');

    expect(component.isUserMode).toBeTrue();
    expect(adminService.getAllUsers.calls.count()).toBe(searchesSoFar);
  });

  it('follows the paginator', () => {
    start();

    component.handlePageEvent(2);

    expect(adminService.getAllUsers).toHaveBeenCalledWith(
      jasmine.anything(),
      2,
    );
  });

  it('searches again when the email field loses the focus after a search', () => {
    start();
    const searchesSoFar = adminService.getAllUsers.calls.count();

    component.focusOut(0);

    expect(adminService.getAllUsers.calls.count()).toBe(searchesSoFar + 1);
  });

  it('does not search on an empty field that was never searched', () => {
    start();
    component.searched = false;
    const searchesSoFar = adminService.getAllUsers.calls.count();

    component.focusOut(0);

    expect(adminService.getAllUsers.calls.count()).toBe(searchesSoFar);
  });

  it('removes a deleted account from the list', () => {
    const user = userFixture({ id: 2 });
    start([user, userFixture({ id: 3 })]);
    adminService.deleteUser.and.returnValue(of(undefined));

    component.deleteUser(user);

    expect(component.users.map((u) => u.id)).toEqual([3]);
    expect(toastr.success.calls.mostRecent().args[1]).toBe('User Deleted');
  });

  it('says nothing when the deleted account was already gone from the list', () => {
    start([userFixture({ id: 3 })]);
    adminService.deleteUser.and.returnValue(of(undefined));

    component.deleteUser(userFixture({ id: 2 }));

    expect(component.users.length).toBe(1);
    expect(toastr.success).not.toHaveBeenCalled();
  });
});
