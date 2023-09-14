import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from 'src/app/dialog/dialog.component';
import { Message } from 'src/app/core/interfaces/message';
import { AdminService } from 'src/app/core/services/admin.service';
import { ToastrService } from 'ngx-toastr';
import { User } from 'src/app/core/interfaces/user';
import { PictureService } from 'src/app/core/services/picture.service';
import { environment } from 'src/environments/environment';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-admin-messages',
  templateUrl: './admin.messages.component.html',
  styleUrls: ['./admin.messages.component.css'],
})
export class AdminMessagesComponent implements OnInit, OnDestroy {
  messages: Message[] = [];
  users: User[] = [];
  imagePath: string = environment.imagePath;
  getUsersSubscription!: Subscription;
  getMainPictureSubscription!: Subscription;
  deleteMessageSubscription!: Subscription;

  constructor(
    private adminService: AdminService,
    private toastr: ToastrService,
    public dialog: MatDialog,
    private pictureService: PictureService
  ) {}

  ngOnInit() {
    this.getUsers();
  }

  ngOnDestroy() {
    this.getUsersSubscription && this.getUsersSubscription.unsubscribe();
    this.getMainPictureSubscription &&
      this.getMainPictureSubscription.unsubscribe();
    this.deleteMessageSubscription &&
      this.deleteMessageSubscription.unsubscribe();
  }

  getUsers() {
    this.getUsersSubscription = this.adminService.getAllUsers().subscribe({
      next: (response: User[]) => {
        this.users = response.filter((user) => user.messagesSent.length !== 0);
        for (let user of this.users) {
          this.getMainPicture(user);
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

  deleteMessage(id: number) {
    this.deleteMessageSubscription = this.adminService
      .deleteMessage(id)
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
          this.toastr.success('Message deleted', 'Message', {
            positionClass: 'toast-bottom-center',
          });
        },
      });
  }

  openDialog(id: number) {
    const dialogRef = this.dialog.open(DialogComponent);

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.deleteMessage(id);
      }
    });
  }

  search(key: string) {
    let results: User[] = [];
    for (let user of this.users) {
      if (user.nickname.toLowerCase().indexOf(key.toLowerCase()) !== -1) {
        results.push(user);
      }
      for (let message of user.messagesSent) {
        if (
          message.content.toLowerCase().indexOf(key.toLowerCase()) !== -1 &&
          !results.includes(user)
        ) {
          results.push(user);
        }
      }
    }
    this.users = results;
    if (results.length === 0 || !key) {
      this.getUsers();
    }
  }
}
