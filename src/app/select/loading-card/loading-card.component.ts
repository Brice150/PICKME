import { Component, EventEmitter, Output, input } from '@angular/core';
import { LoadingComponent } from '../../shared/components/loading/loading.component';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-loading-card',
    imports: [CommonModule, LoadingComponent],
    templateUrl: './loading-card.component.html',
    styleUrl: './loading-card.component.css'
})
export class LoadingCardComponent {
  readonly loading = input<boolean>(true);
  readonly usersNumber = input<number>(0);
  @Output() goToActionEvent: EventEmitter<string> = new EventEmitter<string>();

  goToFirst(): void {
    this.goToActionEvent.emit('first');
  }

  goToProfile(): void {
    this.goToActionEvent.emit('profile');
  }
}
