import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  OnInit,
  Output,
  input,
  inject,
} from '@angular/core';
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
  fb = inject(FormBuilder);
  connectService = inject(ConnectService);

  readonly user = input.required<User>();
  descriptionForm!: FormGroup;
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  ngOnInit(): void {
    this.descriptionForm = this.fb.group({
      description: [
        this.user().description,
        [Validators.minLength(2), Validators.maxLength(500)],
      ],
    });
  }

  updateDescription(): void {
    this.setDescription();
    this.updateEvent.emit('Description Updated');
  }

  setDescription(): void {
    const user = this.user();
    if (user) {
      user.description = this.descriptionForm.get('description')?.value;
      this.descriptionForm.markAsPristine();
    }
  }

  cancel(): void {
    const user = this.user();
    if (user) {
      user.description = this.connectService.connectedUser!.description;
      this.descriptionForm.get('description')?.setValue(user.description);
      this.descriptionForm.markAsPristine();
    }
  }
}
