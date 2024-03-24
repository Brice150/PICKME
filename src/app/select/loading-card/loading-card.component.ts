import { Component, Input } from '@angular/core';
import { LoadingComponent } from '../../shared/components/loading/loading.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading-card',
  standalone: true,
  imports: [CommonModule, LoadingComponent],
  templateUrl: './loading-card.component.html',
  styleUrl: './loading-card.component.css',
})
export class LoadingCardComponent {
  @Input() loading: boolean = true;
}
