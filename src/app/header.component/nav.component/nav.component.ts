import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css']
})

export class NavComponent implements OnInit{
  loggedInUserEmail!: string | null;
  isAdmin!: boolean;

  constructor(private router: Router, 
    private userService: UserService) {}

  ngOnInit() {
    if (sessionStorage.getItem('loggedInUserEmail')===null) {
      this.loggedInUserEmail = null;
    }
    else {
      this.loggedInUserEmail = JSON.parse(sessionStorage.getItem('loggedInUserEmail') || '{}');
      this.getUserRole(this.loggedInUserEmail!);
    }
  }

  getUserRole(email: string) {
    this.userService.findUserByEmail(email).subscribe(
      (response: User) => {
        if (response.userRole === "ROLE_ADMIN") {
          this.isAdmin = true;
        }
        else {
          this.isAdmin = false;
        }
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    )
  }

  extend() {
    let buttons = document.querySelector(".buttons");
    if (!document.querySelector(".active")) {
      buttons?.classList.add("active");
    }
    else {
      buttons?.classList.remove("active");
    }
  }

  logout() {
    sessionStorage.removeItem('loggedInUserEmail');
    this.router.navigate(['/connect'])
    .then(() => {
      window.location.reload();
    });
  }
}