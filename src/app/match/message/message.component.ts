import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Like } from 'src/app/core/interfaces/like';
import { Message } from 'src/app/core/interfaces/message';
import { User } from 'src/app/core/interfaces/user';
import { LikeService } from 'src/app/core/services/like.service';
import { MessageService } from 'src/app/core/services/message.service';
import { DialogComponent } from 'src/app/dialog/dialog.component';
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
  @Output() onRefresh: EventEmitter<User> = new EventEmitter()
  messageForm!: FormGroup;
  isModifying: boolean = false;
  updatedMessage!: Message | null;

  constructor(
    private fb: FormBuilder,
    private likeService: LikeService,
    private router: Router,
    private messageService: MessageService,
    private snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit() {
    this.messageForm = this.fb.group({
      content: ['', [Validators.required, Validators.maxLength(500), Validators.minLength(5)]]
    })
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

  sendMessage(message: Message) {
    message.fkReceiver={"id":this.selectedUser?.id};
    message.fkSender={"id":this.loggedInUser?.id};   
    this.messageService.addMessage(message).subscribe(
      (response: Message) => {
        this.messageForm.get("content")?.reset();
        this.onRefresh.emit(this.selectedUser);
        this.snackBar.open("Message sent", "Dismiss", {duration: 2000});
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    )
  }

  modifyMessage(message: Message) {
    this.updatedMessage = message;
    this.isModifying = true;
  }

  unmodifyMessage() {
    this.messageForm.get("content")?.reset();
    this.updatedMessage = null;
    this.isModifying = false;
  }

  updateMessage(message: Message) {
    message.id=this.updatedMessage?.id!;
    message.fkReceiver={"id":this.selectedUser?.id};
    message.fkSender={"id":this.loggedInUser?.id};
    message.date=this.updatedMessage?.date!;   
    this.messageService.updateMessage(message).subscribe(
      (response: Message) => {
        this.unmodifyMessage();
        this.onRefresh.emit(this.selectedUser);
        this.snackBar.open("Message updated", "Dismiss", {duration: 2000});
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    )
  }

  deleteMessage(message: Message) {
    this.messageService.deleteMessage(message.id).subscribe(
      (response: void) => {
        this.onRefresh.emit(this.selectedUser);
        this.snackBar.open("Message deleted", "Dismiss", {duration: 2000});
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      });
  }

  openDialog(message: Message) {
    const dialogRef = this.dialog.open(DialogComponent);
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteMessage(message);
      }
    });
  }
}
