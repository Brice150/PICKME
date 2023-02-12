import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Like } from 'src/app/core/interfaces/like';
import { Message } from 'src/app/core/interfaces/message';
import { User } from 'src/app/core/interfaces/user';
import { LikeService } from 'src/app/core/services/like.service';
import { MessageService } from 'src/app/core/services/message.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessageComponent implements OnInit{
  imagePath: string = environment.imagePath;
  @Input() selectedUser!: User;
  @Input() messages!: Message[];
  @Input() loggedInUser!: User;

  constructor(
    private likeService: LikeService,
    private router: Router,
    private messageService: MessageService
  ) {}

  ngOnInit() {

  }

  moreInfo(id: number) {
    this.router.navigate(['moreinfo', id, 'match']);
  }

  dislike(user: User) {
    this.likeService.getLikeByFk(this.loggedInUser.id, user.id).subscribe(
      (like: Like) => {
        this.likeService.deleteLike(like.id).subscribe(
          (response: void) => {
            this.router.navigate(['/match'])
            .then(() => {
              window.location.reload();
            });
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

  isToday(message: Message): boolean {
    let today: Date = new Date();
    let messageDate: Date = new Date(message.date);
    let isToday: boolean = 
    messageDate.getFullYear() === today.getFullYear() &&
    messageDate.getMonth() === today.getMonth() &&
    messageDate.getDate() === today.getDate()
    ;
    return isToday;
  }
}
