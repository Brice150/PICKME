import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    LoadingComponent,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  hide: boolean = true;
  invalidLogin: boolean = false;
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private connectService: ConnectService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.loginForm = this.fb.group({
      email: [
        '',
        [Validators.required, Validators.email, Validators.maxLength(30)],
      ],
      password: [
        '',
        [
          Validators.required,
          Validators.maxLength(30),
          Validators.minLength(5),
        ],
      ],
    });
  }

  loginUser(user: User) {
    if (this.loginForm.valid) {
      this.loading = true;
      this.connectService.login(user).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/select']);
        },
        error: (error: HttpErrorResponse) => {
          this.loading = false;
          if (!error.error.error && error.error.includes('Bad credentials')) {
            this.invalidLogin = true;
            this.toastr.error('Wrong email or password !', 'Bad Credentials', {
              positionClass: 'toast-bottom-center',
              toastClass: 'ngx-toastr custom error',
            });
            setTimeout(() => {
              this.invalidLogin = false;
            }, 2000);
          } else {
            this.toastr.error(error.message, 'Error', {
              positionClass: 'toast-bottom-center',
              toastClass: 'ngx-toastr custom error',
            });
          }
        },
        complete: () => {
          this.toastr.success('You are logged in !', 'Logged In', {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom gold',
          });
        },
      });
    }
  }
}
