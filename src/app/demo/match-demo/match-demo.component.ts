import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  input,
  signal,
} from '@angular/core';
import { Gender } from '../../core/enums/gender';
import { MatchCardDemoComponent } from './match-card-demo/match-card-demo.component';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-match-demo',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatchCardDemoComponent],
  templateUrl: './match-demo.component.html',
  styleUrl: './match-demo.component.css',
})
export class MatchDemoComponent implements OnInit {
  imagePath: string = environment.imagePath;
  readonly userGenderSearch = input.required<Gender>();
  readonly selectedMatch = signal<string | undefined>(undefined);
  readonly images = signal<string[]>([]);

  ngOnInit(): void {
    const userGenderSearch = this.userGenderSearch();
    if (userGenderSearch === Gender.MAN) {
      this.imagePath = this.imagePath + 'man-select-demo/';
    } else if (userGenderSearch === Gender.WOMAN) {
      this.imagePath = this.imagePath + 'woman-select-demo/';
    }
    this.images.set([
      this.imagePath + 'Picture1.jpg',
      this.imagePath + 'Picture2.jpg',
    ]);
  }

  isWomanGenderSearch(): boolean {
    return this.userGenderSearch() === Gender.WOMAN;
  }

  selectMatch(match: string): void {
    this.selectedMatch.set(match);
  }

  back(): void {
    this.selectedMatch.set(undefined);
  }
}
