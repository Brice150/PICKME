import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Gender } from '../core/enums/gender';
import { ConnectService } from '../core/services/connect.service';
import { MatchDemoComponent } from './match-demo/match-demo.component';
import { PaginatorDemoComponent } from './paginator-demo/paginator-demo.component';
import { ProfileDemoComponent } from './profile-demo/profile-demo.component';
import { SelectDemoComponent } from './select-demo/select-demo.component';
import { StartDemoComponent } from './start-demo/start-demo.component';

@Component({
  selector: 'app-demo',
  standalone: true,
  imports: [
    CommonModule,
    ProfileDemoComponent,
    SelectDemoComponent,
    MatchDemoComponent,
    StartDemoComponent,
    PaginatorDemoComponent,
  ],
  templateUrl: './demo.component.html',
  styleUrl: './demo.component.css',
})
export class DemoComponent implements OnInit {
  demos: string[] = ['profile', 'select', 'match', 'start'];
  animationDirection: 'previous' | 'next' = 'next';
  currentIndex: number = 0;
  userGender: Gender = Gender.MAN;
  userGenderSearch: Gender = Gender.WOMAN;

  constructor(private connectService: ConnectService) {}

  ngOnInit(): void {
    if (this.connectService.registeredUser) {
      this.userGender = this.connectService.registeredUser.genderAge.gender;
      this.userGenderSearch =
        this.connectService.registeredUser.genderAge.genderSearch;
    }
  }

  previous(): void {
    if (this.currentIndex > 0) {
      this.animationDirection = 'previous';
      this.currentIndex--;
    }
  }

  next(): void {
    if (this.currentIndex < this.demos.length - 1) {
      this.animationDirection = 'next';
      this.currentIndex++;
    }
  }
}
