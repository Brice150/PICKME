import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Gender } from '../../core/enums/gender';

@Component({
  selector: 'app-match-demo',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './match-demo.component.html',
  styleUrl: './match-demo.component.css',
})
export class MatchDemoComponent {
  @Input() userGenderSearch!: Gender;
}
