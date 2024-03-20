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
import { Gender } from '../../core/enums/gender';
import { ConnectService } from '../../core/services/connect.service';

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
  genders: string[] = Object.values(Gender);
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  constructor(
    private fb: FormBuilder,
    private connectService: ConnectService
  ) {}

  ngOnInit(): void {
    this.genderAgeForm = this.fb.group({
      gender: [this.user?.gender, [Validators.required]],
      genderSearch: [this.user?.genderSearch, [Validators.required]],
    });
  }

  updateGenderAge(): void {
    this.setGenderAge();
    this.updateEvent.emit('Gender and Age Updated');
  }

  setGenderAge(): void {
    if (this.user) {
      this.user.gender = this.genderAgeForm.get('gender')?.value;
      this.user.genderSearch = this.genderAgeForm.get('genderSearch')?.value;
      this.user.minAge = this.minAge;
      this.user.maxAge = this.maxAge;
      this.genderAgeForm.markAsPristine();
      this.ageChange = false;
    }
  }

  cancel(): void {
    if (this.user) {
      this.user.gender = this.connectService.connectedUser?.gender!;
      this.user.genderSearch = this.connectService.connectedUser?.genderSearch!;
      this.user.minAge = this.connectService.connectedUser?.minAge!;
      this.user.maxAge = this.connectService.connectedUser?.maxAge!;
      this.genderAgeForm.patchValue({
        gender: this.user.gender,
        genderSearch: this.user.genderSearch,
      });
      this.genderAgeForm.markAsPristine();
      this.ageChange = false;
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
