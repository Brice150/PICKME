import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { DateAdapter, provideNativeDateAdapter } from '@angular/material/core';
import {
  MatDatepickerInputEvent,
  MatDatepickerModule,
} from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSliderModule } from '@angular/material/slider';
import { MatStepperModule } from '@angular/material/stepper';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';
import { Gender } from '../../core/enums/gender';

@Component({
  selector: 'app-register',
  standalone: true,
  providers: [
    provideNativeDateAdapter(),
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false },
    },
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatStepperModule,
    MatSliderModule,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent implements OnInit, OnDestroy {
  destroyed$: Subject<void> = new Subject<void>();
  registerForm!: FormGroup;
  firstFormGroup!: FormGroup;
  secondFormGroup!: FormGroup;
  thirdFormGroup!: FormGroup;
  birthDateExists: boolean = false;
  passwordsMatch: boolean = false;
  birthDate!: Date;
  startDate: Date = new Date(1990, 0, 1);
  minDate: Date;
  minAge: number = 18;
  maxAge: number = 80;
  genders: string[] = Object.values(Gender);

  constructor(
    private fb: FormBuilder,
    private connectService: ConnectService,
    private dateAdapter: DateAdapter<Date>,
    private toastr: ToastrService,
    private router: Router
  ) {
    this.dateAdapter.setLocale('en-GB');
    const currentYear = new Date().getFullYear();
    this.minDate = new Date(
      currentYear - 18,
      new Date().getMonth(),
      new Date().getDate()
    );
  }

  ngOnInit(): void {
    this.firstFormGroup = this.fb.group({
      nickname: [
        '',
        [
          Validators.required,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      ],
      job: [
        '',
        [
          Validators.required,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      ],
      city: [
        '',
        [
          Validators.required,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      ],
    });

    this.secondFormGroup = this.fb.group({
      gender: ['', [Validators.required]],
      genderSearch: ['', [Validators.required]],
    });

    this.thirdFormGroup = this.fb.group({
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
      passwordDuplicate: [
        '',
        [
          Validators.required,
          Validators.maxLength(30),
          Validators.minLength(5),
        ],
      ],
    });

    this.registerForm = this.fb.group({
      firstFormGroup: this.firstFormGroup,
      secondFormGroup: this.secondFormGroup,
      thirdFormGroup: this.thirdFormGroup,
    });
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  registerUser() {
    if (this.registerForm.valid) {
      const user: User = this.setUser();
      this.connectService
        .register(user)
        .pipe(takeUntil(this.destroyed$))
        .subscribe({
          next: (response: User) => {
            this.connectService.connectedUser = response;
            this.router.navigate(['/select']);
          },
          error: (error: HttpErrorResponse) => {
            this.toastr.error(error.message, 'Error', {
              positionClass: 'toast-bottom-center',
              toastClass: 'ngx-toastr custom',
            });
          },
          complete: () => {
            this.toastr.success(
              'You are now registered !',
              'Registration Successful',
              {
                positionClass: 'toast-bottom-center',
                toastClass: 'ngx-toastr custom gold',
              }
            );
          },
        });
    }
  }

  setUser(): User {
    const user: User = {
      nickname: this.registerForm.get('firstFormGroup.nickname')
        ?.value as string,
      job: this.registerForm.get('firstFormGroup.job')?.value as string,
      city: this.registerForm.get('firstFormGroup.city')?.value as string,
      birthDate: this.birthDate,
      gender: this.registerForm.get('secondFormGroup.gender')?.value as string,
      genderSearch: this.registerForm.get('secondFormGroup.genderSearch')
        ?.value as string,
      minAge: this.minAge,
      maxAge: this.maxAge,
      email: this.registerForm.get('thirdFormGroup.email')?.value as string,
      password: this.registerForm.get('thirdFormGroup.password')
        ?.value as string,
    };
    return user;
  }

  addBirthDate(event: MatDatepickerInputEvent<Date>): void {
    if (event.value != null) {
      this.birthDate = event.value;
      this.birthDateExists = true;
    }
  }

  slide(event: Event) {
    const sliderValue = (event.target as HTMLInputElement).value;
    const isMaxAge = (event.target as HTMLInputElement).classList.contains(
      'mat-slider__right-input'
    );
    if (isMaxAge) {
      this.maxAge = parseInt(sliderValue);
    } else {
      this.minAge = parseInt(sliderValue);
    }
  }
}
