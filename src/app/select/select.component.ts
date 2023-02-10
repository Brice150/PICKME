import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { SwiperOptions } from 'swiper';
import { Like } from '../core/interfaces/like';
import { User } from '../core/interfaces/user';
import { LikeService } from '../core/services/like.service';
import { PictureService } from '../core/services/picture.service';
import { UserService } from '../core/services/user.service';
import { AgePipe } from '../shared/pipes/age.pipe';

@Component({
  selector: 'app-select',
  templateUrl: './select.component.html',
  styleUrls: ['./select.component.css']
})
export class SelectComponent implements OnInit{
  imagePath: string = environment.imagePath;
  users: User[] = [];
  loggedInUser!: User;
  minAge: number = 18;
  maxAge: number = 0;
  config: SwiperOptions = {
    grabCursor: true,
    speed: 1500,
    loop: false,
    pagination: { 
      el: '.swiper-pagination', 
      clickable: true, 
      dynamicBullets: true
    },
    spaceBetween: 80,
    breakpoints: {
      0: {
        slidesPerView: 1
      },
      500: {
        slidesPerView: 2
      },
      800: {
        slidesPerView: 3
      },
      1400: {
        slidesPerView: 4
      },
      2000: {
        slidesPerView: 5
      }
    }
  };

  constructor(
    private userService: UserService,
    private pictureService: PictureService,
    private likeService: LikeService,
    private router: Router,
    private agePipe: AgePipe) {}

  ngOnInit() {
    this.getLoggedInUser();
    this.getUsers();
  }

  getLoggedInUser() {
    this.userService.getConnectedUser().subscribe(
      (response: User) => {
        this.loggedInUser = response;
        if (this.agePipe.transform(response.birthDate) + 5 <= 100) {
          this.maxAge = this.agePipe.transform(response.birthDate) + 5;
        }
        else {
          this.maxAge = 100;
        }
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  getUsers() {
    this.userService.getAllUsers().subscribe(
      (response: User[]) => {
        this.users=response;
        this.users = this.users.filter(user => this.minAge <= this.agePipe.transform(user.birthDate)
                      && this.agePipe.transform(user.birthDate) <= this.maxAge);
        for (let user of this.users) {
          this.getMainPicture(user);
        }
      },
      (error: HttpErrorResponse) => {
        alert(error);
      }
    )
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
    let like: any = {
      "date": null, 
      "fkSender": {"id": this.loggedInUser.id}, 
      "fkReceiver": {"id": user.id}};
    this.likeService.addLike(like).subscribe(
      (response: Like) => {
        this.getUsers();
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  moreInfo(id: number) {
    this.router.navigate(['moreinfo', id, 'select']);
  }
}
