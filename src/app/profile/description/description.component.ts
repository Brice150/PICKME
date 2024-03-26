import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';

@Component({
  selector: 'app-description',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
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
      description: [this.user.description],
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
