import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from 'src/environments/environment';
import { User } from '../models/user';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit{
  imagePath: string = environment.imagePath;
  loggedInUser!: User | null;
  loggedInUserEmail!: string | null;
  updateForm!: FormGroup;

  constructor(
    private userService: UserService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    if (sessionStorage.getItem('loggedInUserEmail')===null) {
      this.loggedInUserEmail = null;
    }
    else {
      this.loggedInUserEmail = JSON.parse(sessionStorage.getItem('loggedInUserEmail') || '{}');
    }
    this.updateForm = this.fb.group({
      id: [''],
      userRole: [''],
      enabled: [''],
      locked: [''],
      birthDate: [''],
      nickname: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(2)]],
      city: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(2)]],
      job: ['', [Validators.maxLength(50)]],
      height: ['', [Validators.pattern("^[0-9]*$")]],
      languages: [''],
      activities: [''],
      description: [''],
      gender: ['', [Validators.required]],
      genderSearch: ['', [Validators.required]],
      relationshipType: ['', [Validators.required]],
      alcoholDrinking: [''],
      smokes: [''],
      sportPractice: [''],
      parenthood: [''],
      gamer: [''],
      animals: [''],
      organised: [''],
      personality: [''],
      password: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(5)]]
    })
    this.getLoggedInUser();
  }

  updateUser(user: User) {
    this.userService.updateUser(user).subscribe(
      (response: User) => {
        this.updateForm.get("password")?.reset();
        this.snackBar.open("Content updated", "Dismiss", {duration: 2000});
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  getLoggedInUser() {
    this.userService.findUserByEmail(this.loggedInUserEmail!).subscribe(
      (response: User) => {
        this.loggedInUser = response;
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    )
  }
}
