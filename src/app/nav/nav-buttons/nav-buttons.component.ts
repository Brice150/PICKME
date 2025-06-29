import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output, input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-nav-buttons',
    imports: [CommonModule, RouterModule],
    templateUrl: './nav-buttons.component.html',
    styleUrl: './nav-buttons.component.css'
})
export class NavButtonsComponent {
  readonly hasAdminRole = input<boolean>(false);
  @Output() toggleMenuEvent: EventEmitter<void> = new EventEmitter<void>();
  @Output() logoutEvent: EventEmitter<void> = new EventEmitter<void>();

  toggleMenu(): void {
    this.toggleMenuEvent.emit();
  }

  logout(): void {
    this.logoutEvent.emit();
  }
}
