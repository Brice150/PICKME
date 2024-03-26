import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';

@Component({
  selector: 'app-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './password.component.html',
  styleUrl: './password.component.css',
})
export class PasswordComponent implements OnInit {
  @Input() user!: User;
  passwordForm!: FormGroup;
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  constructor(
    private fb: FormBuilder,
    private connectService: ConnectService
  ) {}

  ngOnInit(): void {
    this.passwordForm = this.fb.group({
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
