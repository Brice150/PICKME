import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
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
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { HttpErrorResponse } from '@angular/common/http';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-register',
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
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    LoadingComponent,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent implements OnInit, OnDestroy {
  fb = inject(FormBuilder);
  connectService = inject(ConnectService);
  toastr = inject(ToastrService);
  router = inject(Router);
  destroyed$: Subject<void> = new Subject<void>();
  hide: boolean = true;
  hideDuplicate: boolean = true;
  registerForm!: FormGroup;
  firstFormGroup!: FormGroup;
  secondFormGroup!: FormGroup;
  thirdFormGroup!: FormGroup;
  passwordsMatch: boolean = false;
  minDate: Date;
  genders: string[] = Object.values(Gender);
  geolocation: Geolocation = {} as Geolocation;
  loading: boolean = false;

  constructor() {
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
      birthDate: ['', [Validators.required]],
      distanceSearch: [100, [Validators.required]],
    });

    this.secondFormGroup = this.fb.group({
      gender: ['', [Validators.required]],
      genderSearch: ['', [Validators.required]],
      minAge: [18, Validators.required],
      maxAge: [40, Validators.required],
    });

    this.thirdFormGroup = this.fb.group(
      {
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
      },
      { validators: this.passwordMatchValidator }
    );

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

  passwordMatchValidator(control: AbstractControl): void {
    const password = control.get('password')?.value;
    const passwordDuplicate = control.get('passwordDuplicate')?.value;

    if (
      control.get('password')!.valid &&
      passwordDuplicate &&
      passwordDuplicate !== '' &&
      password !== passwordDuplicate &&
      !control.get('passwordDuplicate')!.hasError('minlength') &&
      !control.get('passwordDuplicate')!.hasError('maxlength')
    ) {
      control.get('passwordDuplicate')?.setErrors({ passwordMismatch: true });
    }
  }

  registerUser() {
    if (this.registerForm.valid) {
      this.loading = true;
      const user: User = this.setUser();
      this.connectService.register(user).subscribe({
        next: () => {
          this.loading = false;
          this.connectService.registeredUser = user;
          this.router.navigate(['/demo']);
        },
        error: (error: HttpErrorResponse) => {
          this.loading = false;
          this.toastr.error(error.message, 'Error', {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom error',
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
      birthDate: this.registerForm.get('firstFormGroup.birthDate')?.value,
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
}
