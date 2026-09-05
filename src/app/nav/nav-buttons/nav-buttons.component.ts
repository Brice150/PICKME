import { Component, input, output } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-nav-buttons',
  imports: [RouterModule],
  templateUrl: './nav-buttons.component.html',
  styleUrl: './nav-buttons.component.css',
})
export class NavButtonsComponent {
  readonly hasAdminRole = input<boolean>(false);
  readonly toggleMenuEvent = output<void>();
  readonly logoutEvent = output<void>();

  toggleMenu(): void {
    this.toggleMenuEvent.emit();
  }

  logout(): void {
    this.logoutEvent.emit();
  }
}
