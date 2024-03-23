import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSliderModule } from '@angular/material/slider';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { GenderAdmin } from '../core/enums/gender-admin';
import { AdminSearch } from '../core/interfaces/admin-search';
import { User } from '../core/interfaces/user';
import { AdminService } from '../core/services/admin.service';
import { LoadingComponent } from '../shared/components/loading/loading.component';
import { AdminCardComponent } from './admin-card/admin-card.component';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    AdminCardComponent,
    ReactiveFormsModule,
    LoadingComponent,
    MatSliderModule,
    MatCheckboxModule,
  ],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent implements OnInit, OnDestroy {
  users: User[] = [];
  destroyed$: Subject<void> = new Subject<void>();
  loading: boolean = false;
  searched: boolean = false;
  genders: string[] = Object.values(GenderAdmin);
  adminForm!: FormGroup;
  adminSearch: AdminSearch = {} as AdminSearch;

  constructor(
    private toastr: ToastrService,
    private adminService: AdminService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.adminForm = this.fb.group({
      nickname: [''],
      genders: [[]],
      minAge: [18, [Validators.required]],
      maxAge: [80, [Validators.required]],
      distance: [500, [Validators.required]],
      moreDistance: [true],
    });

    this.adminForm
      .get('distance')
      ?.valueChanges.pipe(takeUntil(this.destroyed$))
      .subscribe((value: number) => {
        if (value === 500) {
          this.adminForm.get('moreDistance')?.enable();
          this.adminForm.get('moreDistance')?.setValue(true);
        } else {
          this.adminForm.get('moreDistance')?.disable();
          this.adminForm.get('moreDistance')?.setValue(false);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  search(): void {
    if (this.adminForm.valid) {
      this.loading = true;
      this.setAdminForm();
      this.adminService
        .getAllUsers(this.adminSearch)
        .pipe(takeUntil(this.destroyed$))
        .subscribe({
          next: (users: User[]) => {
            this.users = users;
            this.loading = false;
            this.searched = true;
          },
        });
    }
  }

  setAdminForm(): void {
    this.adminSearch.nickname = this.adminForm.get('nickname')?.value;
    this.adminSearch.minAge = this.adminForm.get('minAge')?.value;
    this.adminSearch.maxAge = this.adminForm.get('maxAge')?.value;
    if (
      this.adminForm.get('genders')?.value.length === 0 ||
      this.adminForm.get('genders')?.value.includes(GenderAdmin.ALL)
    ) {
      this.adminSearch.genders = [
        GenderAdmin.MAN,
        GenderAdmin.WOMAN,
        GenderAdmin.OTHER,
      ];
    } else {
      this.adminSearch.genders = [this.adminForm.get('genders')?.value];
    }
    if (this.adminForm.get('moreDistance')?.value) {
      this.adminSearch.distance = 15650;
    } else {
      this.adminSearch.distance = this.adminForm.get('distance')?.value;
    }
  }

  deleteUser(userToDelete: User): void {
    this.adminService.deleteUser(userToDelete.id!).subscribe({
      next: () => {
        const userIndex = this.users.findIndex(
          (user: User) => user.id === userToDelete.id
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
}
