import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs/internal/Subscription';
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
  styleUrls: ['./message.component.css'],
})
export class MessageComponent implements OnInit, OnDestroy {
  imagePath: string = environment.imagePath;
  @Input() selectedUser!: User;
  @Input() messages!: Message[];
  @Input() loggedInUser!: User;
  @Output() onRefresh: EventEmitter<User> = new EventEmitter();
  messageForm!: FormGroup;
  isModifying: boolean = false;
  updatedMessage!: Message | null;
  getLikeByFkSubscription!: Subscription;
  deleteLikeSubscription!: Subscription;
  sendMessageSubscription!: Subscription;
  updateMessageSubscription!: Subscription;
  deleteMessageSubscription!: Subscription;

  constructor(
    private fb: FormBuilder,
    private likeService: LikeService,
    private router: Router,
    private messageService: MessageService,
    private toastr: ToastrService,
    public dialog: MatDialog
  ) {}

  ngOnInit() {
    this.messageForm = this.fb.group({
      content: [
        '',
        [
          Validators.required,
          Validators.maxLength(500),
          Validators.minLength(5),
        ],
      ],
    });
  }

  ngOnDestroy() {
    this.getLikeByFkSubscription && this.getLikeByFkSubscription.unsubscribe();
    this.deleteLikeSubscription && this.deleteLikeSubscription.unsubscribe();
    this.sendMessageSubscription && this.sendMessageSubscription.unsubscribe();
    this.updateMessageSubscription &&
      this.updateMessageSubscription.unsubscribe();
    this.deleteMessageSubscription &&
      this.deleteMessageSubscription.unsubscribe();
  }

  moreInfo(id: number) {
    this.router.navigate(['moreinfo', id, 'match']);
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
                this.router.navigate(['/match']).then(() => {
                  window.location.reload();
                });
              },
              error: (error: HttpErrorResponse) => {
                this.toastr.error(error.message, 'Server error', {
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

  sendMessage(message: Message) {
    message.fkReceiver = { id: this.selectedUser?.id };
    message.fkSender = { id: this.loggedInUser?.id };
    this.sendMessageSubscription = this.messageService
      .addMessage(message)
      .subscribe({
        next: (response: Message) => {
          this.messageForm.get('content')?.reset();
          this.onRefresh.emit(this.selectedUser);
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Server error', {
            positionClass: 'toast-bottom-center',
          });
        },
        complete: () => {
          this.toastr.success('Message sent', 'Message', {
            positionClass: 'toast-bottom-center',
          });
        },
      });
  }

  modifyMessage(message: Message) {
    this.updatedMessage = message;
    this.isModifying = true;
  }

  unmodifyMessage() {
    this.messageForm.get('content')?.reset();
    this.updatedMessage = null;
    this.isModifying = false;
  }

  updateMessage(message: Message) {
    message.id = this.updatedMessage?.id!;
    message.fkReceiver = { id: this.selectedUser?.id };
    message.fkSender = { id: this.loggedInUser?.id };
    message.date = this.updatedMessage?.date!;
    this.updateMessageSubscription = this.messageService
      .updateMessage(message)
      .subscribe({
        next: (response: Message) => {
          this.unmodifyMessage();
          this.onRefresh.emit(this.selectedUser);
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Server error', {
            positionClass: 'toast-bottom-center',
          });
        },
        complete: () => {
          this.toastr.success('Message updated', 'Message', {
            positionClass: 'toast-bottom-center',
          });
        },
      });
  }

  deleteMessage(messageId: number) {
    this.deleteMessageSubscription = this.messageService
      .deleteMessage(messageId)
      .subscribe({
        next: (response: void) => {
          this.unmodifyMessage();
          this.onRefresh.emit(this.selectedUser);
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Server error', {
            positionClass: 'toast-bottom-center',
          });
        },
        complete: () => {
          this.toastr.success('Message deleted', 'Message', {
            positionClass: 'toast-bottom-center',
          });
        },
      });
  }

  openDialog(messageId: number) {
    const dialogRef = this.dialog.open(DialogComponent);

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.deleteMessage(messageId);
      }
    });
  }
}
