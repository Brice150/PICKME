import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { User } from '../models/user';
import { PictureService } from '../services/picture.service';
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
    private pictureService: PictureService,
    private router: Router) {}

  ngOnInit() {
    this.getUsers();
  }

  getUsers() {
    this.userService.getUsers().subscribe(
      (response: User[]) => {
        this.users=response;
        for (let user of this.users) {
          this.getMainPicture(user);
        }
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

  getDescription(user: User): string | null {
    let description: string | null = user?.description;
    if (user.description && user.description.length > 150) {
      description = user.description.substring(0,147) + "..."
    }
    return description;
  }

  getMainPicture(user: User) {
    let reader = new FileReader();
    if (user.mainPicture) {
      this.pictureService.getPicture(user.mainPicture.toString()).subscribe(
        event => {
        if (event.type === HttpEventType.Response) {
          if (event.body instanceof Array) {

          }
          else {
            let image = new File([event.body!], user.mainPicture!.toString());
            reader.readAsDataURL(image);
            reader.onloadend = (loaded) => {
              user.mainPicture = reader.result!;
            }
          }
        }
        },
        (error: HttpErrorResponse) => {
          alert(error);
        }
      );
    }
    else {
      user.mainPicture = this.imagePath + "No-Image.png";
    }
    (error: HttpErrorResponse) => {
      alert(error);
    }
  }

  like(user: User) {
    
  }

  moreInfo(id: any) {
    this.router.navigate(['moreinfo', id]);
  }
}
