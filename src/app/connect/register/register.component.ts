import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DateAdapter } from '@angular/material/core';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { ToastrService } from 'ngx-toastr';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {
  registerForm!: FormGroup;
  onConfirmEmail: boolean = false;
  startDate: Date = new Date(1990, 0, 1);
  minDate: Date;
  birthDateExists: boolean = false;
  birthDate!: Date;
  registerSubscription!: Subscription;

  constructor(
    private fb: FormBuilder, 
    private connectService: ConnectService,
    private dateAdapter: DateAdapter<Date>,
    private toastr: ToastrService) 
    {
      this.dateAdapter.setLocale("en-GB");
      const currentYear = new Date().getFullYear();
      this.minDate = new Date(currentYear - 18, new Date().getMonth(), new Date().getDate());
    }

  ngOnInit() {
    this.registerForm = this.fb.group({
      nickname: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
      password: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(5)]],
      city: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(2)]],
      gender: ['', [Validators.required]],
      genderSearch: ['', [Validators.required]],
      relationshipType: ['', [Validators.required]]
    })
  }

  ngOnDestroy() {
    this.registerSubscription && this.registerSubscription.unsubscribe();
  }

  registerUser(user: User) {
    user.birthDate = this.birthDate;
    this.registerSubscription = this.connectService.register(user).subscribe({
      next: (response: User) => {
        this.onConfirmEmail=true;
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      },
      complete: () => {
        this.toastr.success("Please confirm your email", "Connection", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
  }
  
  addBirthDate(event: MatDatepickerInputEvent<Date>) {
    if (event.value != null) {
      this.birthDate = event.value;
      this.birthDateExists = true;
    }
  }
}
