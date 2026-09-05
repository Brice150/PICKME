import { NgClass } from '@angular/common';
import {
  Component,
  DestroyRef,
  OnInit,
  inject,
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

  users: User[] = [];
  deletedAccounts: DeletedAccount[] = [];
  loading = false;
  searched = false;
  adminForm!: FormGroup;
  adminSearch: AdminSearch = {} as AdminSearch;
  isUserMode = true;
  isFirstSwitch = true;
  adminStats?: AdminStats;
  // Absent until the first page of results has been rendered.
  private readonly paginator = viewChild<PaginatorComponent>('paginator');

  ngOnInit(): void {
    this.adminForm = this.fb.group({
      email: [''],
      orderBy: [''],
    });

    this.adminService
      .getAdminStats()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (adminStats: AdminStats) => {
          this.adminStats = adminStats;
        },
      });

    this.search(0);
  }

  toggleUserOrDeleted(content: string) {
    if (
      (content === 'deleted' && this.isUserMode) ||
      (content === 'user' && !this.isUserMode)
    ) {
      this.isUserMode = !this.isUserMode;
      if (this.isFirstSwitch) {
        this.searched = false;
        this.isFirstSwitch = !this.isFirstSwitch;
      }
      this.search(0);
    }
  }

  search(page: number): void {
    if (this.adminForm.valid) {
      this.loading = true;
      this.setAdminForm();
      if (this.isUserMode) {
        this.adminService
          .getAllUsers(this.adminSearch, page)
          .pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe({
            next: (users: User[]) => {
              this.users = users;
              this.loading = false;
              this.searched = true;
              const paginator = this.paginator();
              if (paginator && page === 0) {
                paginator.page = page;
              }
            },
            error: () => (this.loading = false),
          });
      } else {
        this.adminService
          .getAllDeletedAccounts(this.adminSearch, page)
          .pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe({
            next: (deletedAccounts: DeletedAccount[]) => {
              this.deletedAccounts = deletedAccounts;
              this.loading = false;
              this.searched = true;
              const paginator = this.paginator();
              if (paginator && page === 0) {
                paginator.page = page;
              }
            },
            error: () => (this.loading = false),
          });
      }
    }
  }

  focusOut(page: number): void {
    if (
      (this.adminForm.get('email')?.value &&
        this.adminForm.get('email')?.value !== '') ||
      this.searched
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
        const userIndex = this.users.findIndex(
          (user: User) => user.id === userToDelete.id,
        );
        if (userIndex !== -1) {
          this.users.splice(userIndex, 1);
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
}
