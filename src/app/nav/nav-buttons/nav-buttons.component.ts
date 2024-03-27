import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-nav-buttons',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './nav-buttons.component.html',
  styleUrl: './nav-buttons.component.css',
})
export class NavButtonsComponent {
  @Input() hasAdminRole: boolean = false;
  @Output() toggleMenuEvent: EventEmitter<void> = new EventEmitter<void>();
  @Output() logoutEvent: EventEmitter<void> = new EventEmitter<void>();

  toggleMenu(): void {
    this.toggleMenuEvent.emit();
  }

  logout(): void {
    this.logoutEvent.emit();
  }
}
