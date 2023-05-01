import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { environment } from 'src/environments/environment';
import { Like } from '../core/interfaces/like';
import { Picture } from '../core/interfaces/picture';
import { User } from '../core/interfaces/user';
import { AdminService } from '../core/services/admin.service';
import { LikeService } from '../core/services/like.service';
import { PictureService } from '../core/services/picture.service';
import { UserService } from '../core/services/user.service';
import { DialogComponent } from '../dialog/dialog.component';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-moreinfo',
  templateUrl: './moreinfo.component.html',
  styleUrls: ['./moreinfo.component.css']
})
export class MoreInfoComponent implements OnInit, OnDestroy {
  notification!: string | null;
  imagePath: string = environment.imagePath;
  user!: User;
  pictures: Picture[] = [];
  loggedInUser!: User;
  mode!: string | null;
  getLoggedInUserSubscription!: Subscription;
  getUserSubscription!: Subscription;
  getAllUserPicturesSubscription!: Subscription;
  getPictureSubscription!: Subscription;
  likeSubscription!: Subscription;
  getLikeByFkSubscription!: Subscription;
  deleteLikeSubscription!: Subscription;
  deleteUserSubscription!: Subscription;

  constructor(
    private userService: UserService,
    private pictureService: PictureService,
    private route: ActivatedRoute,
    private router: Router,
    private likeService: LikeService,
    public dialog: MatDialog,
    private adminService: AdminService,
    private toastr: ToastrService) {}

  ngOnInit() {
    this.getLoggedInUser();
    this.mode = this.route.snapshot.paramMap.get('mode');
    this.getUser(this.route.snapshot.paramMap.get('id'));
  }

  ngOnDestroy() {
    this.getLoggedInUserSubscription && this.getLoggedInUserSubscription.unsubscribe();
    this.getUserSubscription && this.getUserSubscription.unsubscribe();
    this.getAllUserPicturesSubscription && this.getAllUserPicturesSubscription.unsubscribe();
    this.getPictureSubscription && this.getPictureSubscription.unsubscribe();
    this.likeSubscription && this.likeSubscription.unsubscribe();
    this.getLikeByFkSubscription && this.getLikeByFkSubscription.unsubscribe();
    this.deleteLikeSubscription && this.deleteLikeSubscription.unsubscribe();
    this.deleteUserSubscription && this.deleteUserSubscription.unsubscribe();
  }

  getLoggedInUser() {
    this.getLoggedInUserSubscription = this.userService.getConnectedUser().subscribe({
      next: (response: User) => {
        this.loggedInUser = response;
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
  }

  getUser(id: any) {
    this.getUserSubscription = this.userService.getUserById(id).subscribe({
      next: (response: User) => {
        this.user = response;
        this.getPictures(id!);
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
  }

  getPictures(id: any) {
    this.getAllUserPicturesSubscription = this.pictureService.getAllUserPictures(id).subscribe({
      next: (response: Picture[]) => {
        if (response.length > 0) {
          for (let picture of response) {
            let reader = new FileReader();
            this.getPictureSubscription = this.pictureService.getPicture(picture.content.toString()).subscribe({
              next: event => {
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
              error: (error: HttpErrorResponse) => {
                this.toastr.error(error.message, "Server error", {
                  positionClass: "toast-bottom-center" 
                })
              }
            })
            this.pictures.push(picture);
          }   
          }
        },
        error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
    const loaderWrapper = document.getElementById('loaderWrapper');
    loaderWrapper!.style.display = 'none';
  }

  like(user: User) {
    let like: any = {
      "date": null, 
      "fkSender": {"id": this.loggedInUser.id}, 
      "fkReceiver": {"id": user.id}};
      this.likeSubscription = this.likeService.addLike(like).subscribe({
      next: (response: string) => {
        this.router.navigate(['/'+this.mode]);
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

  dislike(user: User) {
    this.getLikeByFkSubscription = this.likeService.getLikeByFk(this.loggedInUser.id, user.id).subscribe({
      next: (like: Like) => {
        this.deleteLikeSubscription = this.likeService.deleteLike(like.id).subscribe({
          next: (response: void) => {
            this.router.navigate(['/'+this.mode])
          },
          error: (error: HttpErrorResponse) => {
            this.toastr.error(error.message, "Server error", {
              positionClass: "toast-bottom-center" 
            })
          },
          complete: () => {
            this.toastr.success("Disliked "+user.nickname, "Dislike", {
              positionClass: "toast-bottom-center" 
            })
          }
        })
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
  }

  openDialog() {
    const dialogRef = this.dialog.open(DialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteUser();
      }
    })
  }

  deleteUser() {
    this.deleteUserSubscription = this.adminService.deleteUser(this.user.email).subscribe({
      next: (response: void) => {
        this.router.navigate(['/admin'])
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      },
      complete: () => {
        this.toastr.success("User deleted : "+this.user.nickname, "User", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
  }

}
