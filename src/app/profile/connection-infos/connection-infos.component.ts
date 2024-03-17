import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { User } from '../../core/interfaces/user';

@Component({
  selector: 'app-connection-infos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './connection-infos.component.html',
  styleUrl: './connection-infos.component.css',
})
export class ConnectionInfosComponent implements OnInit {
  @Input() user?: User;
  connectionInfosForm!: FormGroup;
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.connectionInfosForm = this.fb.group({
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
    this.connectionInfosForm.markAsPristine();
  }

  setConnectionInfos() {
    if (this.user) {
      this.user.password = this.connectionInfosForm.get('password')?.value;
      //TODO : remove after backend saved
    }
  }
}
