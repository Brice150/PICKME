import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { User } from '../../core/interfaces/user';
import { HttpErrorResponse } from '@angular/common/http';
import { ConnectService } from '../../core/services/connect.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  loginForm!: FormGroup;
  invalidLogin: boolean = false;
  destroyed$: Subject<void> = new Subject<void>();

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

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  loginUser(user: User) {
    if (this.loginForm.valid) {
      this.connectService
        .login(user)
        .pipe(takeUntil(this.destroyed$))
        .subscribe({
          next: (response: User) => {
            this.connectService.connectedUser = response;
            this.router.navigate(['/select']);
          },
          error: (error: HttpErrorResponse) => {
            if (error.message.includes('Bad credentials')) {
              this.invalidLogin = true;
              this.toastr.error(
                'Wrong email or password !',
                'Bad Credentials',
                {
                  positionClass: 'toast-bottom-center',
                  toastClass: 'ngx-toastr custom',
                }
              );
              setTimeout(() => {
                this.invalidLogin = false;
              }, 2000);
            } else {
              this.toastr.error(error.message, 'Error', {
                positionClass: 'toast-bottom-center',
                toastClass: 'ngx-toastr custom',
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
