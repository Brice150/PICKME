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
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-description',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './description.component.html',
  styleUrl: './description.component.css',
})
export class DescriptionComponent implements OnInit {
  @Input() user!: User;
  descriptionForm!: FormGroup;
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  constructor(
    private fb: FormBuilder,
    private connectService: ConnectService
  ) {}

  ngOnInit(): void {
    this.descriptionForm = this.fb.group({
      description: [
        this.user.description,
        [Validators.minLength(2), Validators.maxLength(500)],
      ],
    });
  }

  updateDescription(): void {
    this.setDescription();
    this.updateEvent.emit('Description Updated');
  }

  setDescription(): void {
    if (this.user) {
      this.user.description = this.descriptionForm.get('description')?.value;
      this.descriptionForm.markAsPristine();
    }
  }

  cancel(): void {
    if (this.user) {
      this.user.description = this.connectService.connectedUser!.description;
      this.descriptionForm.get('description')?.setValue(this.user.description);
      this.descriptionForm.markAsPristine();
    }
  }
}
