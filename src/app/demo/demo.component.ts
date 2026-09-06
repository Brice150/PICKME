import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { Gender } from '../core/enums/gender';
import { ConnectService } from '../core/services/connect.service';
import { MatchDemoComponent } from './match-demo/match-demo.component';
import { PaginatorDemoComponent } from './paginator-demo/paginator-demo.component';
import { ProfileDemoComponent } from './profile-demo/profile-demo.component';
import { SelectDemoComponent } from './select-demo/select-demo.component';
import { StartDemoComponent } from './start-demo/start-demo.component';

@Component({
  selector: 'app-demo',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
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
  private readonly connectService = inject(ConnectService);

  readonly demos: string[] = ['profile', 'select', 'match', 'start'];
  animationDirection: 'previous' | 'next' = 'next';
  readonly currentIndex = signal(0);
  readonly userGender = signal<Gender>(Gender.MAN);
  readonly userGenderSearch = signal<Gender>(Gender.WOMAN);

  ngOnInit(): void {
    const registeredUser = this.connectService.registeredUser();
    if (registeredUser) {
      this.userGender.set(registeredUser.genderAge.gender);
      this.userGenderSearch.set(registeredUser.genderAge.genderSearch);
    }
  }

  previous(): void {
    if (this.currentIndex() > 0) {
      this.animationDirection = 'previous';
      this.currentIndex.update((index: number) => index - 1);
    }
  }

  next(): void {
    if (this.currentIndex() < this.demos.length - 1) {
      this.animationDirection = 'next';
      this.currentIndex.update((index: number) => index + 1);
    }
  }
}
