import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DateAdapter } from '@angular/material/core';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { User } from 'src/app/models/user';
import { ConnectService } from 'src/app/services/connect.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  onConfirmEmail: boolean = false;
  startDate: Date = new Date(1990, 0, 1);
  minDate: Date;
  birthDateExists: boolean = false;
  birthDate!: Date;

  constructor(
    private fb: FormBuilder, 
    private connectService: ConnectService,
    private dateAdapter: DateAdapter<Date>) 
    {
      this.dateAdapter.setLocale('en-GB');
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

  registerUser(user: User) {
    user.birthDate = this.birthDate;
    this.connectService.register(user).subscribe(
      (response: User) => {
        this.onConfirmEmail=true;
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }
  
  addBirthDate(event: MatDatepickerInputEvent<Date>) {
    if (event.value != null) {
      this.birthDate = event.value;
      this.birthDateExists = true;
    }
  }
}
