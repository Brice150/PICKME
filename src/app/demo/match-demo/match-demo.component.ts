import { CommonModule } from '@angular/common';
import { Component, OnInit, input } from '@angular/core';
import { Gender } from '../../core/enums/gender';
import { MatchCardDemoComponent } from './match-card-demo/match-card-demo.component';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'app-match-demo',
    imports: [CommonModule, MatchCardDemoComponent],
    templateUrl: './match-demo.component.html',
    styleUrl: './match-demo.component.css'
})
export class MatchDemoComponent implements OnInit {
  imagePath: string = environment.imagePath;
  readonly userGenderSearch = input.required<Gender>();
  selectedMatch?: string;
  images: string[] = [];

  ngOnInit(): void {
    const userGenderSearch = this.userGenderSearch();
    if (userGenderSearch === Gender.MAN) {
      this.imagePath = this.imagePath + 'man-select-demo/';
    } else if (userGenderSearch === Gender.WOMAN) {
      this.imagePath = this.imagePath + 'woman-select-demo/';
    }
    this.images = [
      this.imagePath + 'Picture1.jpg',
      this.imagePath + 'Picture2.jpg',
    ];
  }

  isWomanGenderSearch(): boolean {
    return this.userGenderSearch() === Gender.WOMAN;
  }

  selectMatch(match: string): void {
    this.selectedMatch = match;
  }

  back(): void {
    this.selectedMatch = undefined;
  }
}
