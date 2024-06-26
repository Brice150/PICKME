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
import { ConnectService } from '../../core/services/connect.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-main-infos',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatSliderModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './main-infos.component.html',
  styleUrl: './main-infos.component.css',
})
export class MainInfosComponent implements OnInit {
  @Input() user!: User;
  mainInfosForm!: FormGroup;
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  constructor(
    private fb: FormBuilder,
    private connectService: ConnectService
  ) {}

  ngOnInit(): void {
    this.mainInfosForm = this.fb.group({
      nickname: [
        this.user.nickname,
        [
          Validators.required,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      ],
      job: [
        this.user.job,
        [
          Validators.required,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      ],
      distanceSearch: [
        this.user.geolocation?.distanceSearch,
        [Validators.required],
      ],
      height: [this.user.height, Validators.required],
    });
  }

  updateMainInfos(): void {
    this.setMainInfos();
    this.updateEvent.emit('Main Infos Updated');
  }

  setMainInfos(): void {
    if (this.user) {
      this.user.nickname = this.mainInfosForm.get('nickname')?.value;
      this.user.job = this.mainInfosForm.get('job')?.value;
      this.user.geolocation.distanceSearch =
        this.mainInfosForm.get('distanceSearch')?.value;
      this.user.height = this.mainInfosForm.get('height')?.value;
      this.mainInfosForm.markAsPristine();
    }
  }

  cancel(): void {
    if (this.user) {
      this.user.nickname = this.connectService.connectedUser!.nickname;
      this.user.job = this.connectService.connectedUser!.job;
      this.user.geolocation.distanceSearch =
        this.connectService.connectedUser!.geolocation?.distanceSearch;
      this.mainInfosForm.patchValue({
        nickname: this.user.nickname,
        job: this.user.job,
        distanceSearch: this.user.geolocation.distanceSearch,
        height: this.user.height,
      });
      this.mainInfosForm.markAsPristine();
    }
  }
}
