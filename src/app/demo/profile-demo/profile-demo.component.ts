import { CommonModule } from '@angular/common';
import {
  CUSTOM_ELEMENTS_SCHEMA,
  Component,
  Input,
  OnInit,
} from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { environment } from '../../../environments/environment';
import { Gender } from '../../core/enums/gender';

@Component({
    selector: 'app-profile-demo',
    imports: [CommonModule, MatExpansionModule],
    templateUrl: './profile-demo.component.html',
    styleUrl: './profile-demo.component.css',
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProfileDemoComponent implements OnInit {
  imagePath: string = environment.imagePath;
  @Input() userGender!: Gender;

  ngOnInit(): void {
    if (this.userGender === Gender.MAN) {
      this.imagePath = this.imagePath + 'man-profile-demo/';
    } else if (this.userGender === Gender.WOMAN) {
      this.imagePath = this.imagePath + 'woman-profile-demo/';
    }
  }
}
