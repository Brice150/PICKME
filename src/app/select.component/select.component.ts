import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { User } from '../models/user';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-select',
  templateUrl: './select.component.html',
  styleUrls: ['./select.component.css']
})
export class SelectComponent implements OnInit{
  imagePath: string = environment.imagePath;
  users: User[] = [];

  constructor(
    private userService: UserService,
    private router: Router) {}

  ngOnInit() {
    this.getUsers();
  }

  getUsers() {
    this.userService.getUsers().subscribe(
      (response: User[]) => {
        this.users=response;
      },
      (error: HttpErrorResponse) => {
        alert(error);
      }
    )
  }

  getAge(user: User): number{
    let age: number = 0;
    let today: Date = new Date();
    let birthDate: Date = new Date(user.birthDate);
    let dateDifference: Date = new Date(
      today.getFullYear() - birthDate.getFullYear(), 
      today.getMonth() - birthDate.getMonth(), 
      today.getDate() - birthDate.getDate()
    )
    age = dateDifference.getFullYear();
    age = Number(String(age).slice(-2));
    return age;
  }

  getDescription(user: User): string {
    let description: string = user?.description;
    if (user.description && user.description.length > 150) {
      description = user.description.substring(0,147) + "..."
    }
    return description
  }

  getImage(user: User): string {
    return this.imagePath + "No-Image.png"
  }

  like(user: User) {
    
  }

  moreInfo(id: any) {
    this.router.navigate(['moreinfo', id]);
  }
}
