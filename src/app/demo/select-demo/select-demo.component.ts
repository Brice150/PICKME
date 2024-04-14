import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Gender } from '../../core/enums/gender';

@Component({
  selector: 'app-select-demo',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './select-demo.component.html',
  styleUrl: './select-demo.component.css',
})
export class SelectDemoComponent {
  @Input() userGenderSearch!: Gender;
}
