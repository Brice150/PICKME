import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-password',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './password.component.html',
  styleUrl: './password.component.css',
})
export class PasswordComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly connectService = inject(ConnectService);

  readonly user = input.required<User>();
  readonly hide = signal(true);
  readonly hideDuplicate = signal(true);
  passwordForm!: FormGroup;
  readonly updateEvent = output<string>();

  ngOnInit(): void {
    this.passwordForm = this.fb.group(
      {
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
      { validators: this.passwordMatchValidator },
    );
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

  updateConnectionInfos(): void {
    this.setConnectionInfos();
    this.updateEvent.emit('Connection Infos Updated');
  }

  setConnectionInfos(): void {
    const user = this.user();
    if (user) {
      user.password = this.passwordForm.get('password')?.value;
      this.passwordForm.markAsPristine();
    }
  }

  cancel(): void {
    const user = this.user();
    if (user) {
      user.password = this.connectService.connectedUser()!.password;
      this.passwordForm.patchValue({
        password: null,
        passwordDuplicate: null,
      });
      this.passwordForm.markAsUntouched();
      this.passwordForm.markAsPristine();
    }
  }
}
