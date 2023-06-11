import { Component } from '@angular/core';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent {
  activeUsers: boolean = true;
  activeMessages: boolean = false;

  onObjects() {
    this.activeUsers = false;
    this.activeMessages = false;
  }
  
  onUsers() {
    this.activeUsers = true;
    this.activeMessages = false;
  }

  onMessages() {
    this.activeUsers = false;
    this.activeMessages = true;
  }
}
