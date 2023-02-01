import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { Like } from '../core/interfaces/like';
import { User } from '../core/interfaces/user';
import { LikeService } from '../core/services/like.service';
import { PictureService } from '../core/services/picture.service';
import { UserService } from '../core/services/user.service';
import { MessageService } from '../core/services/message.service';

@Component({
  selector: 'app-match',
  templateUrl: './match.component.html',
  styleUrls: ['./match.component.css']
})
export class MatchComponent {
  imagePath: string = environment.imagePath;
  users: User[] = [];
  selectedUser!: User;
  loggedInUser!: User;
  messagesNumber: number = 1; 

  constructor(
    private userService: UserService,
    private pictureService: PictureService,
    private likeService: LikeService,
    private messageService: MessageService,
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
    this.userService.getAllUsersThatMatched().subscribe(
      (response: User[]) => {
        this.users=response;
        if (this.users.length !== 0) {
          this.selectedUser = this.users[0];
        } 
        for (let user of this.users) {
          this.getMainPicture(user);
          this.getUserMessagesNumber(user);
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

  getUserMessagesNumber(user: User) {
    this.messageService.getUserMessagesNumber(user.id).subscribe(
      (response: number) => {
        user.messagesNumber = response;
      },
      (error: HttpErrorResponse) => {
        alert(error);
      }
    )
  }

  dislike(user: User) {
    this.likeService.getLikeByFk(this.loggedInUser.id, user.id).subscribe(
      (like: Like) => {
        this.likeService.deleteLike(like.id).subscribe(
          (response: void) => {
            this.getUsers();
          },
          (error: HttpErrorResponse) => {
            alert(error.message);
          }
        );
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  moreInfo(id: any) {
    this.router.navigate(['moreinfo', id, 'match']);
  }

  viewMessage(user: User) {
    this.selectedUser = user;
  }
}
