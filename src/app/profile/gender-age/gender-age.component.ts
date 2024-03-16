import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { User } from '../../core/interfaces/user';
import { MatSliderModule } from '@angular/material/slider';

@Component({
  selector: 'app-gender-age',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatSliderModule],
  templateUrl: './gender-age.component.html',
  styleUrl: './gender-age.component.css',
})
export class GenderAgeComponent implements OnInit {
  @Input() user?: User;
  genderAgeForm!: FormGroup;
  minAge: number = 18;
  maxAge: number = 80;
  ageChange: boolean = false;
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.genderAgeForm = this.fb.group({
      gender: [this.user?.gender, [Validators.required]],
      genderSearch: [this.user?.genderSearch, [Validators.required]],
    });
  }

  updateGenderAge(): void {
    this.setGenderAge();
    this.updateEvent.emit('Gender and Age Updated');
    this.genderAgeForm.markAsPristine();
    this.ageChange = false;
  }

  setGenderAge() {
    if (this.user) {
      this.user.gender = this.genderAgeForm.get('gender')?.value;
      this.user.genderSearch = this.genderAgeForm.get('genderSearch')?.value;
      this.user.minAge = this.minAge;
      this.user.maxAge = this.maxAge;
    }
  }

  slideAge(event: Event) {
    const sliderValue = (event.target as HTMLInputElement).value;
    const isMaxAge = (event.target as HTMLInputElement).classList.contains(
      'mat-slider__right-input'
    );
    if (isMaxAge) {
      this.maxAge = parseInt(sliderValue);
    } else {
      this.minAge = parseInt(sliderValue);
    }
    this.ageChange = true;
  }
}
