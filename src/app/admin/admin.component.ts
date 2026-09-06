import { NgClass } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
  inject,
  signal,
  viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSliderModule } from '@angular/material/slider';
import { ToastrService } from 'ngx-toastr';
import { AdminSearch } from '../core/interfaces/admin-search';
import { User } from '../core/interfaces/user';
import { AdminService } from '../core/services/admin.service';
import { LoadingComponent } from '../shared/components/loading/loading.component';
import { PaginatorComponent } from '../shared/components/paginator/paginator.component';
import { UserCardComponent } from './user-card/user-card.component';
import { DeletedAccount } from '../core/interfaces/deleted-account';
import { DeletedAccountCardComponent } from './deleted-account-card/deleted-account-card.component';
import { AdminStats } from '../core/interfaces/admin-stats';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-admin',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    NgClass,
    UserCardComponent,
    ReactiveFormsModule,
    LoadingComponent,
    MatSliderModule,
    MatCheckboxModule,
    PaginatorComponent,
    DeletedAccountCardComponent,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent implements OnInit {
  private readonly toastr = inject(ToastrService);
  private readonly adminService = inject(AdminService);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  readonly users = signal<User[]>([]);
  readonly deletedAccounts = signal<DeletedAccount[]>([]);
  readonly loading = signal(false);
  readonly searched = signal(false);
  readonly isUserMode = signal(true);
  readonly adminStats = signal<AdminStats | undefined>(undefined);
  readonly adminForm: FormGroup = this.fb.group({
    email: [''],
    orderBy: [''],
  });
  adminSearch: AdminSearch = {} as AdminSearch;
  private isFirstSwitch = true;
  // Absent until the first page of results has been rendered.
  private readonly paginator = viewChild<PaginatorComponent>('paginator');

  ngOnInit(): void {
    this.adminService
      .getAdminStats()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (adminStats: AdminStats) => {
          this.adminStats.set(adminStats);
        },
      });

    this.search(0);
  }

  toggleUserOrDeleted(content: string) {
    if (
      (content === 'deleted' && this.isUserMode()) ||
      (content === 'user' && !this.isUserMode())
    ) {
      this.isUserMode.update((userMode: boolean) => !userMode);
      if (this.isFirstSwitch) {
        this.searched.set(false);
        this.isFirstSwitch = false;
      }
      this.search(0);
    }
  }

  search(page: number): void {
    if (this.adminForm.valid) {
      this.loading.set(true);
      this.setAdminForm();
      if (this.isUserMode()) {
        this.adminService
          .getAllUsers(this.adminSearch, page)
          .pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe({
            next: (users: User[]) => {
              this.users.set(users);
              this.searchDone(page);
            },
            error: () => this.loading.set(false),
          });
      } else {
        this.adminService
          .getAllDeletedAccounts(this.adminSearch, page)
          .pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe({
            next: (deletedAccounts: DeletedAccount[]) => {
              this.deletedAccounts.set(deletedAccounts);
              this.searchDone(page);
            },
            error: () => this.loading.set(false),
          });
      }
    }
  }

  focusOut(page: number): void {
    if (
      (this.adminForm.get('email')?.value &&
        this.adminForm.get('email')?.value !== '') ||
      this.searched()
    ) {
      this.search(page);
    }
  }

  setAdminForm(): void {
    this.adminSearch.email = this.adminForm.get('email')?.value;
    this.adminSearch.orderBy = this.adminForm.get('orderBy')?.value;
  }

  deleteUser(userToDelete: User): void {
    this.adminService.deleteUser(userToDelete.id!).subscribe({
      next: () => {
        const remaining = this.users().filter(
          (user: User) => user.id !== userToDelete.id,
        );
        if (remaining.length !== this.users().length) {
          this.users.set(remaining);
          this.toastr.success('User has been deleted', 'User Deleted', {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          });
        }
      },
    });
  }

  handlePageEvent(pageIndex: number) {
    this.search(pageIndex);
  }

  /** Closes a search, and brings the paginator back to the front when it started over. */
  private searchDone(page: number): void {
    this.loading.set(false);
    this.searched.set(true);
    if (page === 0) {
      this.paginator()?.page.set(page);
    }
  }
}
