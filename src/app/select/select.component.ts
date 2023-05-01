import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { environment } from 'src/environments/environment';
import { SwiperOptions } from 'swiper';
import { User } from '../core/interfaces/user';
import { LikeService } from '../core/services/like.service';
import { PictureService } from '../core/services/picture.service';
import { UserService } from '../core/services/user.service';
import { AgePipe } from '../shared/pipes/age.pipe';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-select',
  templateUrl: './select.component.html',
  styleUrls: ['./select.component.css']
})
export class SelectComponent implements OnInit, OnDestroy{
  notification!: string | null;
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
  getLoggedInUserSubscription!: Subscription;
  getUsersSubscription!: Subscription;
  getMainPictureSubscription!: Subscription;
  likeSubscription!: Subscription;

  constructor(
    private userService: UserService,
    private pictureService: PictureService,
    private likeService: LikeService,
    private router: Router,
    private agePipe: AgePipe,
    private toastr: ToastrService) {}

  ngOnInit() {
    this.getLoggedInUser();
    this.getUsers();
  }

  ngOnDestroy() {
    this.getLoggedInUserSubscription && this.getLoggedInUserSubscription.unsubscribe();
    this.getUsersSubscription && this.getUsersSubscription.unsubscribe();
    this.getMainPictureSubscription && this.getMainPictureSubscription.unsubscribe();
    this.likeSubscription && this.likeSubscription.unsubscribe();
  }

  getLoggedInUser() {
    this.getLoggedInUserSubscription = this.userService.getConnectedUser().subscribe({
      next: (response: User) => {
        this.loggedInUser = response;
        if (this.agePipe.transform(response.birthDate) + 5 <= 100) {
          this.maxAge = this.agePipe.transform(response.birthDate) + 5;
        }
        else {
          this.maxAge = 100;
        }
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
  }

  getUsers() {
    this.getUsersSubscription = this.userService.getAllUsers().subscribe({
      next: (response: User[]) => {
        this.users= response;
        this.users = this.users.filter(user => this.minAge <= this.agePipe.transform(user.birthDate)
                      && this.agePipe.transform(user.birthDate) <= this.maxAge);
        for (let user of this.users) {
          this.getMainPicture(user);
        }
        const loaderWrapper = document.getElementById('loaderWrapper');
        loaderWrapper!.style.display = 'none';
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
  }

  getMainPicture(user: User) {
    let reader = new FileReader();
    if (user.mainPicture) {
      this.getMainPictureSubscription = this.pictureService.getPicture(user.mainPicture.toString()).subscribe({
        next: event => {
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
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, "Server error", {
            positionClass: "toast-bottom-center" 
          })
        }
      })
    }
    else {
      user.mainPicture = this.imagePath + "No-Image.png";
    }
  }

  like(user: User) {
    let like: any = {
      "date": null, 
      "fkSender": {"id": this.loggedInUser.id}, 
      "fkReceiver": {"id": user.id}};
      this.likeSubscription = this.likeService.addLike(like).subscribe({
      next: (response: string) => {
        this.getUsers();
        this.toastr.success("Liked "+user.nickname, "Like", {
          positionClass: "toast-bottom-center" 
        })
        if (response !== "") {
          this.notification = response;
          this.toastr.success("Match with "+response, "Match", {
            positionClass: "toast-bottom-center" 
          })
          setTimeout(() => {
            this.notification = null;
          }, 3000)
        }
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
  }

  moreInfo(id: number) {
    this.router.navigate(['moreinfo', id, 'select']);
  }
}
