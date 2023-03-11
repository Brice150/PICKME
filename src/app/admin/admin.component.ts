import { Component } from '@angular/core';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent {
  isConnected!: boolean;
  activeUsers: boolean = true;
  activeMessages: boolean = false;

  ngOnInit() {
    if (sessionStorage.getItem('loggedInUserEmail')===null) {
      this.isConnected = false;
    }
    else {
      this.isConnected = true;
    }
  }

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
