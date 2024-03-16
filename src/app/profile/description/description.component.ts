import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { User } from '../../core/interfaces/user';

@Component({
  selector: 'app-description',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './description.component.html',
  styleUrl: './description.component.css',
})
export class DescriptionComponent implements OnInit {
  @Input() user?: User;
  descriptionForm!: FormGroup;
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.descriptionForm = this.fb.group({
      description: [this.user?.description],
    });
  }

  updateDescription(): void {
    this.setDescription();
    this.updateEvent.emit('Description Updated');
    this.descriptionForm.markAsPristine();
  }

  setDescription() {
    if (this.user) {
      this.user.description = this.descriptionForm.get('description')?.value;
    }
  }
}
