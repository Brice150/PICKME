import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { ConnectService } from 'src/app/services/connect.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit{
  loginForm!: FormGroup;
  invalidLogin: boolean = false;
  
  constructor(
    private fb: FormBuilder, 
    private connectService: ConnectService,
    private router: Router) {}

    ngOnInit() {
      this.loginForm = this.fb.group({
        email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
        password: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(5)]]
      })
    }

    loginUser(user: User) {
      this.connectService.login(user).subscribe(
        (response: any) => {
          sessionStorage.setItem('loggedInUserEmail', JSON.stringify(user.email));
          this.router.navigate(['/profile'])
          .then(() => {
            window.location.reload();
          });
        },
        () => {
          this.invalidLogin = true;
          setTimeout(() => {
            this.invalidLogin = false;
          }, 2000);
        }
      );
    }
}
