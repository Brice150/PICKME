import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { CommonModule } from '@angular/common';
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
import { Gender } from '../../core/enums/gender';
import { Geolocation } from '../../core/interfaces/geolocation';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';

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
  birthDateTouched: boolean = false;
  passwordsMatch: boolean = false;
  birthDate!: Date;
  startDate: Date = new Date(1990, 0, 1);
  minDate: Date;
  genders: string[] = Object.values(Gender);
  geolocation: Geolocation = {} as Geolocation;

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
      distanceSearch: [100, [Validators.required]],
    });

    this.secondFormGroup = this.fb.group({
      gender: ['', [Validators.required]],
      genderSearch: ['', [Validators.required]],
      minAge: [18, Validators.required],
      maxAge: [40, Validators.required],
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

    this.connectService
      .getGeolocation()
      .pipe(takeUntil(this.destroyed$))
      .subscribe({
        next: (geolocation: Geolocation) => {
          this.geolocation.latitude = geolocation.latitude;
          this.geolocation.longitude = geolocation.longitude;
          if (geolocation.city === geolocation.country_capital) {
            navigator.geolocation.getCurrentPosition(
              (position) => {
                this.geolocation.latitude = position.coords.latitude.toString();
                this.geolocation.longitude =
                  position.coords.longitude.toString();
              },
              (error) => {
                // Do nothing
              }
            );
          }
        },
      });
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  registerUser() {
    if (this.registerForm.valid) {
      const user: User = this.setUser();
      this.connectService.register(user).subscribe({
        next: () => {
          this.connectService.registeredUser = user;
          this.router.navigate(['/demo']);
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

  loginUser(user: User): void {
    this.connectService.login(user).subscribe({
      next: () => {
        this.router.navigate(['/select']);
      },
      complete: () => {
        this.toastr.success('You are logged in !', 'Logged In', {
          positionClass: 'toast-bottom-center',
          toastClass: 'ngx-toastr custom gold',
        });
      },
    });
  }

  setUser(): User {
    const user: User = {
      nickname: this.registerForm.get('firstFormGroup.nickname')
        ?.value as string,
      job: this.registerForm.get('firstFormGroup.job')?.value as string,
      birthDate: this.birthDate,
      genderAge: {
        gender: this.registerForm.get('secondFormGroup.gender')
          ?.value as Gender,
        genderSearch: this.registerForm.get('secondFormGroup.genderSearch')
          ?.value as Gender,
        minAge: this.registerForm.get('secondFormGroup.minAge')?.value,
        maxAge: this.registerForm.get('secondFormGroup.maxAge')?.value,
      },
      email: this.registerForm.get('thirdFormGroup.email')?.value as string,
      password: this.registerForm.get('thirdFormGroup.password')
        ?.value as string,
      geolocation: {
        latitude: this.geolocation.latitude,
        longitude: this.geolocation.longitude,
        distanceSearch: this.registerForm.get('firstFormGroup.distanceSearch')
          ?.value as number,
      },
    };
    return user;
  }

  addBirthDate(event: MatDatepickerInputEvent<Date>): void {
    if (event.value != null) {
      this.birthDate = event.value;
      this.birthDateExists = true;
    }
  }

  closed(): void {
    this.birthDateTouched = true;
  }
}
