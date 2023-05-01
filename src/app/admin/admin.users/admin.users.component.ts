import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from 'src/app/dialog/dialog.component';
import { User } from 'src/app/core/interfaces/user';
import { AdminService } from 'src/app/core/services/admin.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { environment } from 'src/environments/environment';
import { PictureService } from 'src/app/core/services/picture.service';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin.users.component.html',
  styleUrls: ['./admin.users.component.css']
})
export class AdminUsersComponent implements OnInit, OnDestroy{
  users: User[]=[];
  imagePath: string = environment.imagePath;
  getUsersSubscription!: Subscription;
  getMainPictureSubscription!: Subscription;
  deleteUserSubscription!: Subscription;

  constructor(
    private adminService: AdminService,
    private toastr: ToastrService,
    private pictureService: PictureService,
    public dialog: MatDialog,
    private router: Router) {}
  
  ngOnInit() {
    this.getUsers();
  }

  ngOnDestroy() {
    this.getUsersSubscription && this.getUsersSubscription.unsubscribe();
    this.getMainPictureSubscription && this.getMainPictureSubscription.unsubscribe();
    this.deleteUserSubscription && this.deleteUserSubscription.unsubscribe();
  }

  getUsers() {
    this.getUsersSubscription = this.adminService.getAllUsers().subscribe({
      next: (response: User[]) => {
        this.users = response;
        for (let user of this.users) {
          this.getMainPicture(user);
        }
        const loaderWrapper = document.getElementById('loaderWrapper');
        loaderWrapper!.style.display = 'none';
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
  }

  getMainPicture(user: User) {
    let reader = new FileReader();
    if (user.mainPicture) {
      this.getMainPictureSubscription = this.pictureService.getPicture(user.mainPicture.toString()).subscribe({
        next: event => {
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
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, "Server error", {
            positionClass: "toast-bottom-center" 
          })
        }
      })
    }
    else {
      user.mainPicture = this.imagePath + "No-Image.png";
    }
  }

  deleteUser(email: string) {
    this.deleteUserSubscription = this.adminService.deleteUser(email).subscribe({
      next: (response: void) => {
        this.getUsers();
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      },
      complete: () => {
        this.toastr.success("User deleted", "User", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
  }

  openDialog(email: string) {
    const dialogRef = this.dialog.open(DialogComponent);

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteUser(email);
      }
    })
  }

  search(key: string){
    let results: User[] = [];
    for (let user of this.users) {
      if (user.nickname.toLowerCase().indexOf(key.toLowerCase())!== -1
      || user.userRole.toLowerCase().indexOf(key.toLowerCase())!== -1) {
        results.push(user);
      }
    }
    this.users = results;
    if (results.length === 0 ||!key) {
      this.getUsers();
    }
  }

  moreInfo(id: any) {
    this.router.navigate(['moreinfo', id, 'admin']);
  }
}
