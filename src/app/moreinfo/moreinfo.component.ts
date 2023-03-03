import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { Like } from '../core/interfaces/like';
import { Picture } from '../core/interfaces/picture';
import { User } from '../core/interfaces/user';
import { AdminService } from '../core/services/admin.service';
import { LikeService } from '../core/services/like.service';
import { PictureService } from '../core/services/picture.service';
import { UserService } from '../core/services/user.service';
import { DialogComponent } from '../dialog/dialog.component';

@Component({
  selector: 'app-moreinfo',
  templateUrl: './moreinfo.component.html',
  styleUrls: ['./moreinfo.component.css']
})
export class MoreInfoComponent implements OnInit {
  notification!: string | null;
  imagePath: string = environment.imagePath;
  user!: User;
  pictures: Picture[] = [];
  loggedInUser!: User;
  mode!: string | null;

  constructor(
    private userService: UserService,
    private pictureService: PictureService,
    private route: ActivatedRoute,
    private router: Router,
    private likeService: LikeService,
    public dialog: MatDialog,
    private adminService: AdminService,
    private snackBar: MatSnackBar) {}

  ngOnInit() {
    this.getLoggedInUser();
    this.mode = this.route.snapshot.paramMap.get('mode');
    this.getUser(this.route.snapshot.paramMap.get('id'));
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

  getUser(id: any) {
    this.userService.getUserById(id).subscribe(
      (response: User) => {
        this.user = response;
        this.getPictures(id!);
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  getPictures(id: any) {
    this.pictureService.getAllUserPictures(id).subscribe(
      (response: Picture[]) => {
        if (response.length > 0) {
          for (let picture of response) {
            let reader = new FileReader();
            this.pictureService.getPicture(picture.content.toString()).subscribe(
              event => {
              if (event.type === HttpEventType.Response) {
                if (event.body instanceof Array) {
                            
                }
                else {
                  let image = new File([event.body!], picture.content.toString());
                  reader.readAsDataURL(image);
                  reader.onloadend = (loaded) => {
                    picture.content = reader.result!;
                  }
                }
              }
              },
              (error: HttpErrorResponse) => {
                alert(error);
              }
            );   
            this.pictures.push(picture);
          }   
          }
        },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
    const loaderWrapper = document.getElementById('loaderWrapper');
    loaderWrapper!.style.display = 'none';
  }

  like(user: User) {
    let like: any = {
      "date": null, 
      "fkSender": {"id": this.loggedInUser.id}, 
      "fkReceiver": {"id": user.id}};
    this.likeService.addLike(like).subscribe(
      (response: string) => {
        this.snackBar.open("Like sent", "Dismiss", {duration: 2000});
        if (response !== null) {
          this.notification = response;
          setTimeout(() => {
            this.notification = null;
            this.router.navigate(['/'+this.mode]);
          }, 3000);
        }
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  dislike(user: User) {
    this.likeService.getLikeByFk(this.loggedInUser.id, user.id).subscribe(
      (like: Like) => {
        this.likeService.deleteLike(like.id).subscribe(
          (response: void) => {
            this.router.navigate(['/'+this.mode]);
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

  openDialog() {
    const dialogRef = this.dialog.open(DialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteUser();
      }
    });
  }

  deleteUser() {
    this.adminService.deleteUser(this.user.email).subscribe(
      (response: void) => {
        this.router.navigate(['/admin']);
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

}
