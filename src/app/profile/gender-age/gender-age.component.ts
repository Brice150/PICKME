import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSliderModule } from '@angular/material/slider';
import { Gender } from '../../core/enums/gender';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';

@Component({
    selector: 'app-gender-age',
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatSliderModule,
        MatFormFieldModule,
        MatSelectModule,
    ],
    templateUrl: './gender-age.component.html',
    styleUrl: './gender-age.component.css'
})
export class GenderAgeComponent implements OnInit {
  @Input() user!: User;
  genderAgeForm!: FormGroup;
  genders: string[] = Object.values(Gender);
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  constructor(
    private fb: FormBuilder,
    private connectService: ConnectService
  ) {}

  ngOnInit(): void {
    this.genderAgeForm = this.fb.group({
      gender: [this.user.genderAge?.gender, [Validators.required]],
      genderSearch: [this.user.genderAge?.genderSearch, [Validators.required]],
      minAge: [this.user.genderAge?.minAge, [Validators.required]],
      maxAge: [this.user.genderAge?.maxAge, [Validators.required]],
    });
  }

  updateGenderAge(): void {
    this.setGenderAge();
    this.updateEvent.emit('Gender and Age Updated');
  }

  setGenderAge(): void {
    if (this.user) {
      this.user.genderAge.gender = this.genderAgeForm.get('gender')?.value;
      this.user.genderAge.genderSearch =
        this.genderAgeForm.get('genderSearch')?.value;
      this.user.genderAge.minAge = this.genderAgeForm.get('minAge')?.value;
      this.user.genderAge.maxAge = this.genderAgeForm.get('maxAge')?.value;
      this.genderAgeForm.markAsPristine();
    }
  }

  cancel(): void {
    if (this.user) {
      this.user.genderAge.gender =
        this.connectService.connectedUser!.genderAge?.gender!;
      this.user.genderAge.genderSearch =
        this.connectService.connectedUser!.genderAge?.genderSearch!;
      this.user.genderAge.minAge =
        this.connectService.connectedUser!.genderAge?.minAge!;
      this.user.genderAge.maxAge =
        this.connectService.connectedUser!.genderAge?.maxAge!;
      this.genderAgeForm.patchValue({
        gender: this.user.genderAge.gender,
        genderSearch: this.user.genderAge.genderSearch,
        minAge: this.user.genderAge.minAge,
        maxAge: this.user.genderAge.maxAge,
      });
      this.genderAgeForm.markAsPristine();
    }
  }
}
