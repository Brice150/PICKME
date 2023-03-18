import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from 'src/app/dialog/dialog.component';
import { User } from 'src/app/core/interfaces/user';
import { AdminService } from 'src/app/core/services/admin.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { environment } from 'src/environments/environment';
import { PictureService } from 'src/app/core/services/picture.service';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin.users.component.html',
  styleUrls: ['./admin.users.component.css']
})
export class AdminUsersComponent implements OnInit{
  users: User[]=[];
  imagePath: string = environment.imagePath;

  constructor(
    private adminService: AdminService,
    private toastr: ToastrService,
    private pictureService: PictureService,
    public dialog: MatDialog,
    private router: Router) {}
  
  ngOnInit() {
    this.getUsers();
  }

  getUsers() {
    this.adminService.getAllUsers().subscribe(
      (response: User[]) => {
        this.users = response;
        for (let user of this.users) {
          this.getMainPicture(user);
        }
        const loaderWrapper = document.getElementById('loaderWrapper');
        loaderWrapper!.style.display = 'none';
      },
      (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        });
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
          this.toastr.error(error.message, "Server error", {
            positionClass: "toast-bottom-center" 
          });
        }
      );
    }
    else {
      user.mainPicture = this.imagePath + "No-Image.png";
    }
    (error: HttpErrorResponse) => {
      this.toastr.error(error.message, "Server error", {
        positionClass: "toast-bottom-center" 
      });
    }
  }

  deleteUser(email: string) {
    this.adminService.deleteUser(email).subscribe(
      (response: void) => {
        this.getUsers();
        this.toastr.success("User deleted", "User", {
          positionClass: "toast-bottom-center" 
        });
      },
      (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        });
      }
    )
  }

  openDialog(email: string) {
    const dialogRef = this.dialog.open(DialogComponent);

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteUser(email);
      }
    });
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
