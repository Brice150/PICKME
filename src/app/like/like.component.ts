import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { Like } from '../core/interfaces/like';
import { User } from '../core/interfaces/user';
import { LikeService } from '../core/services/like.service';
import { PictureService } from '../core/services/picture.service';
import { UserService } from '../core/services/user.service';

@Component({
  selector: 'app-like',
  templateUrl: './like.component.html',
  styleUrls: ['./like.component.css']
})
export class LikeComponent {
  imagePath: string = environment.imagePath;
  users: User[] = [];
  loggedInUser!: User;

  constructor(
    private userService: UserService,
    private pictureService: PictureService,
    private likeService: LikeService,
    private router: Router) {}

  ngOnInit() {
    this.getUsers();
    this.getLoggedInUser();
  }

  getLoggedInUser() {
    this.userService.getConnectedUser().subscribe(
      (response: User) => {
        this.loggedInUser = response;
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  getUsers() {
    this.userService.getAllUsersThatLiked().subscribe(
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

  moreInfo(id: any) {
    this.router.navigate(['moreinfo', id, 'like']);
  }
}
