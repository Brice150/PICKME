import { Component, input, output } from '@angular/core';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-loading-card',
  imports: [LoadingComponent],
  templateUrl: './loading-card.component.html',
  styleUrl: './loading-card.component.css',
})
export class LoadingCardComponent {
  readonly loading = input<boolean>(true);
  readonly usersNumber = input<number>(0);
  readonly goToActionEvent = output<string>();

  goToFirst(): void {
    this.goToActionEvent.emit('first');
  }

  goToProfile(): void {
    this.goToActionEvent.emit('profile');
  }
}
