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
  selector: 'app-main-infos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatSliderModule],
  templateUrl: './main-infos.component.html',
  styleUrl: './main-infos.component.css',
})
export class MainInfosComponent implements OnInit {
  @Input() user?: User;
  mainInfosForm!: FormGroup;
  height: number = 160;
  heightChange: boolean = false;
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.mainInfosForm = this.fb.group({
      nickname: [
        this.user?.nickname,
        [
          Validators.required,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      ],
      job: [
        this.user?.job,
        [
          Validators.required,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      ],
      city: [
        this.user?.city,
        [
          Validators.required,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      ],
    });
  }

  updateMainInfos(): void {
    this.setMainInfos();
    this.updateEvent.emit('Main Infos Updated');
    this.mainInfosForm.markAsPristine();
    this.heightChange = false;
  }

  setMainInfos() {
    if (this.user) {
      this.user.nickname = this.mainInfosForm.get('nickname')?.value;
      this.user.job = this.mainInfosForm.get('job')?.value;
      this.user.city = this.mainInfosForm.get('city')?.value;
      this.user.height = this.height;
    }
  }

  slideHeight(event: Event) {
    const sliderValue = (event.target as HTMLInputElement).value;
    this.height = parseInt(sliderValue);
    this.heightChange = true;
  }
}
