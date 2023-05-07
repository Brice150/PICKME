import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy{
  loginForm!: FormGroup;
  invalidLogin: boolean = false;
  loginSubscription!: Subscription;
  
  constructor(
    private fb: FormBuilder, 
    private connectService: ConnectService,
    private router: Router,
    private toastr: ToastrService) {}

    ngOnInit() {
      this.loginForm = this.fb.group({
        email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
        password: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(5)]]
      })
    }

    ngOnDestroy() {
      this.loginSubscription && this.loginSubscription.unsubscribe();
    }

    loginUser(user: User) {
      this.loginSubscription = this.connectService.login(user).subscribe({
        next: (response: any) => {
          sessionStorage.setItem("loggedInUserEmail", JSON.stringify(user.email));
          this.router.navigate(["/select"]);
        },
        error: (error: HttpErrorResponse) => {
          this.invalidLogin = true;
          if (error.error.includes("Bad credentials")) {
            this.toastr.error("Wrong email or password !", "Connection", {
              positionClass: "toast-bottom-center" 
            })
          }
          setTimeout(() => {
            this.invalidLogin = false;
          }, 2000)
        },
        complete: () => {
          this.toastr.success("Logged in !", "Connection", {
            positionClass: "toast-bottom-center" 
          })
        }
      })
    }
}
