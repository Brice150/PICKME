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
import { MatSliderModule } from '@angular/material/slider';
import { ConnectService } from '../../core/services/connect.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-main-infos',
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
  fb = inject(FormBuilder);
  connectService = inject(ConnectService);

  readonly user = input.required<User>();
  mainInfosForm!: FormGroup;
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  ngOnInit(): void {
    this.mainInfosForm = this.fb.group({
      nickname: [
        this.user().nickname,
        [
          Validators.required,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      ],
      job: [
        this.user().job,
        [
          Validators.required,
          Validators.maxLength(30),
          Validators.minLength(2),
        ],
      ],
      distanceSearch: [
        this.user().geolocation?.distanceSearch,
        [Validators.required],
      ],
      height: [this.user().height, Validators.required],
    });
  }

  updateMainInfos(): void {
    this.setMainInfos();
    this.updateEvent.emit('Main Infos Updated');
  }

  setMainInfos(): void {
    const user = this.user();
    if (user) {
      user.nickname = this.mainInfosForm.get('nickname')?.value;
      user.job = this.mainInfosForm.get('job')?.value;
      user.geolocation.distanceSearch =
        this.mainInfosForm.get('distanceSearch')?.value;
      user.height = this.mainInfosForm.get('height')?.value;
      this.mainInfosForm.markAsPristine();
    }
  }

  cancel(): void {
    const user = this.user();
    if (user) {
      user.nickname = this.connectService.connectedUser!.nickname;
      user.job = this.connectService.connectedUser!.job;
      user.geolocation.distanceSearch =
        this.connectService.connectedUser!.geolocation?.distanceSearch;
      this.mainInfosForm.patchValue({
        nickname: user.nickname,
        job: user.job,
        distanceSearch: user.geolocation.distanceSearch,
        height: user.height,
      });
      this.mainInfosForm.markAsPristine();
    }
  }
}
