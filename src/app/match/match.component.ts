import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { Like } from '../core/interfaces/like';
import { User } from '../core/interfaces/user';
import { LikeService } from '../core/services/like.service';
import { PictureService } from '../core/services/picture.service';
import { UserService } from '../core/services/user.service';
import { MessageService } from '../core/services/message.service';
import { Message } from '../core/interfaces/message';
import { SwiperOptions } from 'swiper';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-match',
  templateUrl: './match.component.html',
  styleUrls: ['./match.component.css'],
})
export class MatchComponent implements OnInit, OnDestroy {
  imagePath: string = environment.imagePath;
  users: User[] = [];
  selectedUser!: User;
  loggedInUser!: User;
  messagesNumber: number = 1;
  messages: Message[] = [];
  config: SwiperOptions = {
    grabCursor: true,
    speed: 1500,
    loop: false,
    pagination: {
      el: '.swiper-pagination',
      clickable: true,
      dynamicBullets: true,
    },
    spaceBetween: 80,
    breakpoints: {
      0: {
        slidesPerView: 1,
      },
      500: {
        slidesPerView: 2,
      },
      800: {
        slidesPerView: 3,
      },
      1400: {
        slidesPerView: 4,
      },
      2000: {
        slidesPerView: 5,
      },
    },
  };
  getLoggedInUserSubscription!: Subscription;
  getUsersSubscription!: Subscription;
  getMainPictureSubscription!: Subscription;
  getUserMessagesNumberSubscription!: Subscription;
  getLikeByFkSubscription!: Subscription;
  deleteLikeSubscription!: Subscription;
  getSelectedUserMessagesSubscription!: Subscription;
  getMessageSenderSubscription!: Subscription;

  constructor(
    private userService: UserService,
    private pictureService: PictureService,
    private likeService: LikeService,
    private messageService: MessageService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.getLoggedInUser();
    this.getUsers();
  }

  ngOnDestroy() {
    this.getLoggedInUserSubscription &&
      this.getLoggedInUserSubscription.unsubscribe();
    this.getUsersSubscription && this.getUsersSubscription.unsubscribe();
    this.getMainPictureSubscription &&
      this.getMainPictureSubscription.unsubscribe();
    this.getUserMessagesNumberSubscription &&
      this.getUserMessagesNumberSubscription.unsubscribe();
    this.getLikeByFkSubscription && this.getLikeByFkSubscription.unsubscribe();
    this.deleteLikeSubscription && this.deleteLikeSubscription.unsubscribe();
    this.getSelectedUserMessagesSubscription &&
      this.getSelectedUserMessagesSubscription.unsubscribe();
    this.getMessageSenderSubscription &&
      this.getMessageSenderSubscription.unsubscribe();
  }

  getLoggedInUser() {
    this.getLoggedInUserSubscription = this.userService
      .getConnectedUser()
      .subscribe({
        next: (response: User) => {
          this.loggedInUser = response;
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Server error', {
            positionClass: 'toast-bottom-center',
          });
        },
      });
  }

  getUsers() {
    this.getUsersSubscription = this.userService
      .getAllUsersThatMatched()
      .subscribe({
        next: (response: User[]) => {
          this.users = response;
          if (this.users.length !== 0) {
            this.selectedUser = this.users[0];
            this.getSelectedUserMessages(this.selectedUser.id);
          }
          for (let user of this.users) {
            this.getMainPicture(user);
            this.getUserMessagesNumber(user);
          }
          const loaderWrapper = document.getElementById('loaderWrapper');
          loaderWrapper!.style.display = 'none';
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Server error', {
            positionClass: 'toast-bottom-center',
          });
        },
      });
  }

  getMainPicture(user: User) {
    let reader = new FileReader();
    if (user.mainPicture) {
      this.getMainPictureSubscription = this.pictureService
        .getPicture(user.mainPicture.toString())
        .subscribe({
          next: (event) => {
            if (event.type === HttpEventType.Response) {
              if (event.body instanceof Array) {
              } else {
                let image = new File(
                  [event.body!],
                  user.mainPicture!.toString()
                );
                reader.readAsDataURL(image);
                reader.onloadend = (loaded) => {
                  user.mainPicture = reader.result!;
                };
              }
            }
          },
          error: (error: HttpErrorResponse) => {
            this.toastr.error(error.message, 'Server error', {
              positionClass: 'toast-bottom-center',
            });
          },
        });
    } else {
      user.mainPicture = this.imagePath + 'No-Image.png';
    }
  }

  getUserMessagesNumber(user: User) {
    this.getUserMessagesNumberSubscription = this.messageService
      .getUserMessagesNumber(user.id)
      .subscribe({
        next: (response: number) => {
          user.messagesNumber = response;
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Server error', {
            positionClass: 'toast-bottom-center',
          });
        },
      });
  }

  dislike(user: User) {
    this.getLikeByFkSubscription = this.likeService
      .getLikeByFk(this.loggedInUser.id, user.id)
      .subscribe({
        next: (like: Like) => {
          this.deleteLikeSubscription = this.likeService
            .deleteLike(like.id)
            .subscribe({
              next: (response: void) => {
                this.getUsers();
              },
              error: (error: HttpErrorResponse) => {
                this.toastr.error(error.message, 'Server error', {
                  positionClass: 'toast-bottom-center',
                });
              },
              complete: () => {
                this.toastr.success('Disliked ' + user.nickname, 'Dislike', {
                  positionClass: 'toast-bottom-center',
                });
              },
            });
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Server error', {
            positionClass: 'toast-bottom-center',
          });
        },
      });
  }

  moreInfo(id: any) {
    this.router.navigate(['moreinfo', id, 'match']);
  }

  viewMessage(user: User) {
    this.selectedUser = user;
    this.getSelectedUserMessages(user.id);
  }

  refreshUserMessages(user: User) {
    this.selectedUser = user;
    this.getSelectedUserMessages(user.id);
    this.getUserMessagesNumber(user);
  }

  getSelectedUserMessages(userId: number) {
    this.getSelectedUserMessagesSubscription = this.messageService
      .getAllUserMessages(userId)
      .subscribe({
        next: (response: Message[]) => {
          this.messages = response;
          for (let message of this.messages) {
            this.getMessageSender(message);
          }
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Server error', {
            positionClass: 'toast-bottom-center',
          });
        },
      });
  }

  getMessageSender(message: Message) {
    this.getMessageSenderSubscription = this.messageService
      .getMessageSender(message.id)
      .subscribe({
        next: (response: User) => {
          message.sender = response.nickname;
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Server error', {
            positionClass: 'toast-bottom-center',
          });
        },
      });
  }

  search(key: string) {
    let results: User[] = [];
    for (let user of this.users) {
      if (user.nickname.toLowerCase().indexOf(key.toLowerCase()) !== -1) {
        results.push(user);
      }
    }
    this.users = results;
    if (results.length === 0 || !key) {
      this.getUsers();
    }
  }
}
