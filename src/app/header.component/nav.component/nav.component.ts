import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css']
})

export class NavComponent{

  constructor(private router: Router) {}

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