import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { User } from '../core/interfaces/user';
import { AdminCardComponent } from './admin-card/admin-card.component';
import { AdminService } from '../core/services/admin.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Subject, takeUntil } from 'rxjs';
import { LoadingComponent } from '../shared/components/loading/loading.component';
import { Gender } from '../core/enums/gender';
import { MatSliderModule } from '@angular/material/slider';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    AdminCardComponent,
    ReactiveFormsModule,
    LoadingComponent,
    MatSliderModule,
  ],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent implements OnInit, OnDestroy {
  users: User[] = [];
  destroyed$: Subject<void> = new Subject<void>();
  loading: boolean = false;
  searched: boolean = false;
  genders: string[] = Object.values(Gender);
  adminForm!: FormGroup;

  constructor(
    private toastr: ToastrService,
    private adminService: AdminService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.adminForm = this.fb.group({
      nickname: ['', [Validators.required]],
      gender: ['', [Validators.required]],
      minAge: [18, [Validators.required]],
      maxAge: [80, [Validators.required]],
    });
  }

  search(): void {
    console.log(this.adminForm.value);

    this.loading = true;
    this.adminService
      .getAllUsers()
      .pipe(takeUntil(this.destroyed$))
      .subscribe({
        next: (users: User[]) => {
          this.users = users;
          this.loading = false;
          this.searched = true;
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Error', {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          });
        },
      });
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
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
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, 'Error', {
          positionClass: 'toast-bottom-center',
          toastClass: 'ngx-toastr custom',
        });
      },
    });
  }
}
