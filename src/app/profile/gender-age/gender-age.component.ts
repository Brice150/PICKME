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
  styleUrl: './gender-age.component.css',
})
export class GenderAgeComponent implements OnInit {
  fb = inject(FormBuilder);
  connectService = inject(ConnectService);

  readonly user = input.required<User>();
  genderAgeForm!: FormGroup;
  genders: string[] = Object.values(Gender);
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  ngOnInit(): void {
    this.genderAgeForm = this.fb.group({
      gender: [this.user().genderAge?.gender, [Validators.required]],
      genderSearch: [
        this.user().genderAge?.genderSearch,
        [Validators.required],
      ],
      minAge: [this.user().genderAge?.minAge, [Validators.required]],
      maxAge: [this.user().genderAge?.maxAge, [Validators.required]],
    });
  }

  updateGenderAge(): void {
    this.setGenderAge();
    this.updateEvent.emit('Gender and Age Updated');
  }

  setGenderAge(): void {
    const user = this.user();
    if (user) {
      user.genderAge.gender = this.genderAgeForm.get('gender')?.value;
      user.genderAge.genderSearch =
        this.genderAgeForm.get('genderSearch')?.value;
      user.genderAge.minAge = this.genderAgeForm.get('minAge')?.value;
      user.genderAge.maxAge = this.genderAgeForm.get('maxAge')?.value;
      this.genderAgeForm.markAsPristine();
    }
  }

  cancel(): void {
    const user = this.user();
    if (user) {
      user.genderAge.gender =
        this.connectService.connectedUser!.genderAge?.gender!;
      user.genderAge.genderSearch =
        this.connectService.connectedUser!.genderAge?.genderSearch!;
      user.genderAge.minAge =
        this.connectService.connectedUser!.genderAge?.minAge!;
      user.genderAge.maxAge =
        this.connectService.connectedUser!.genderAge?.maxAge!;
      this.genderAgeForm.patchValue({
        gender: user.genderAge.gender,
        genderSearch: user.genderAge.genderSearch,
        minAge: user.genderAge.minAge,
        maxAge: user.genderAge.maxAge,
      });
      this.genderAgeForm.markAsPristine();
    }
  }
}
