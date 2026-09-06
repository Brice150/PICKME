import {
  ChangeDetectionStrategy,
  Component,
  CUSTOM_ELEMENTS_SCHEMA,
  input,
  OnInit,
} from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { environment } from '../../../environments/environment';
import { Gender } from '../../core/enums/gender';

@Component({
  selector: 'app-profile-demo',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatExpansionModule],
  templateUrl: './profile-demo.component.html',
  styleUrl: './profile-demo.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ProfileDemoComponent implements OnInit {
  imagePath: string = environment.imagePath;
  readonly userGender = input.required<Gender>();

  ngOnInit(): void {
    const userGender = this.userGender();
    if (userGender === Gender.MAN) {
      this.imagePath = this.imagePath + 'man-profile-demo/';
    } else if (userGender === Gender.WOMAN) {
      this.imagePath = this.imagePath + 'woman-profile-demo/';
    }
  }
}
