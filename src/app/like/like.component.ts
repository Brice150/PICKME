import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { environment } from 'src/environments/environment';
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
  notification!: string | null;

  constructor(
    private userService: UserService,
    private pictureService: PictureService,
    private likeService: LikeService,
    private router: Router,
    private toastr: ToastrService) {}

  ngOnInit() {
    this.getLoggedInUser();
    this.getUsers();
  }

  getLoggedInUser() {
    this.userService.getConnectedUser().subscribe(
      (response: User) => {
        this.loggedInUser = response;
      },
      (error: HttpErrorResponse) => {
        this.toastr.error(error.message);
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
        const loaderWrapper = document.getElementById('loaderWrapper');
        loaderWrapper!.style.display = 'none';
      },
      (error: HttpErrorResponse) => {
        this.toastr.error(error.message);
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
          this.toastr.error(error.message);
        }
      );
    }
    else {
      user.mainPicture = this.imagePath + "No-Image.png";
    }
    (error: HttpErrorResponse) => {
      this.toastr.error(error.message);
    }
  }

  like(user: User) {
    let like: any = {
      "date": null, 
      "fkSender": {"id": this.loggedInUser.id}, 
      "fkReceiver": {"id": user.id}};
    this.likeService.addLike(like).subscribe(
      (response: string) => {
        this.getUsers();
        this.toastr.success("Like sent", 'toast-bottom-center');
        if (response !== null) {
          this.notification = response;
          setTimeout(() => {
            this.notification = null;
          }, 3000);
        }
      },
      (error: HttpErrorResponse) => {
        this.toastr.error(error.message);
      }
    );
  }

  moreInfo(id: any) {
    this.router.navigate(['moreinfo', id, 'like']);
  }
}
