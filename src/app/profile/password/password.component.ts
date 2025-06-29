import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
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
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
    ],
    templateUrl: './password.component.html',
    styleUrl: './password.component.css'
})
export class PasswordComponent implements OnInit {
  @Input() user!: User;
  hide: boolean = true;
  hideDuplicate: boolean = true;
  passwordForm!: FormGroup;
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  constructor(
    private fb: FormBuilder,
    private connectService: ConnectService
  ) {}

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
      { validators: this.passwordMatchValidator }
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
    if (this.user) {
      this.user.password = this.passwordForm.get('password')?.value;
      this.passwordForm.markAsPristine();
    }
  }

  cancel(): void {
    if (this.user) {
      this.user.password = this.connectService.connectedUser!.password;
      this.passwordForm.patchValue({
        password: null,
        passwordDuplicate: null,
      });
      this.passwordForm.markAsUntouched();
      this.passwordForm.markAsPristine();
    }
  }
}
