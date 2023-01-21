import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { DialogComponent } from '../dialog/dialog.component';
import { Picture } from '../core/interfaces/picture';
import { User } from '../core/interfaces/user';
import { PictureService } from '../core/services/picture.service';
import { UserService } from '../core/services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit{
  imagePath: string = environment.imagePath;
  loggedInUser!: User | null;
  loggedInUserEmail!: string | null;
  updateForm!: FormGroup;
  pictures: Picture[] = [];
  picturesData!: FormData;

  constructor(
    private router: Router,
    private userService: UserService,
    private pictureService: PictureService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit() {
    this.updateForm = this.fb.group({
      id: [''],
      userRole: [''],
      enabled: [''],
      locked: [''],
      birthDate: [''],
      nickname: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(2)]],
      city: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(2)]],
      job: ['', [Validators.maxLength(50)]],
      height: ['', [Validators.pattern("^[0-9]*$")]],
      languages: [''],
      activities: [''],
      description: [''],
      gender: ['', [Validators.required]],
      genderSearch: ['', [Validators.required]],
      relationshipType: ['', [Validators.required]],
      alcoholDrinking: [''],
      smokes: [''],
      sportPractice: [''],
      parenthood: [''],
      gamer: [''],
      animals: [''],
      organised: [''],
      personality: [''],
      password: ['', [Validators.required, Validators.maxLength(50), Validators.minLength(5)]]
    })
    this.getLoggedInUser(0);
  }

  getLoggedInUser(wait: number) {
    setTimeout(() => {
      this.userService.getConnectedUser().subscribe(
        (response: User) => {
          this.loggedInUser = response;
          this.getMainPicture(this.loggedInUser);
          this.getPictures(this.loggedInUser.id);
        },
        (error: HttpErrorResponse) => {
          alert(error.message);
        }
      )
    }, wait);
  }

  updateUser(user: User) {
    this.userService.updateUser(user).subscribe(
      (response: User) => {
        this.updateForm.get("password")?.reset();
        this.snackBar.open("Content updated", "Dismiss", {duration: 2000});
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  deleteUser() {
    this.userService.deleteUser(this.loggedInUser?.email!).subscribe(
      (response: void) => {
        sessionStorage.removeItem('loggedInUserEmail');
        this.router.navigate(['/connect'])
        .then(() => {
          window.location.reload();
        });
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
          alert(error);
        }
      );
    }
    else {
      user.mainPicture = this.imagePath + "No-Image.png";
    }
    (error: HttpErrorResponse) => {
      alert(error);
    }
  }

  getPictures(fkUser: any) {
    this.pictures = [];
    this.pictureService.getAllUserPictures(fkUser).subscribe(
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
    )
  }

  addPicture(files: File[]) {
    this.picturesData = new FormData();
    for (const file of files) {
      this.picturesData.append('content', file, file.name);
    }
    this.pictureService.addPicture(this.picturesData).subscribe(
      event => {
        console.log(event);
      },
      (error: HttpErrorResponse) => {
        alert(error);
      }
    );
    this.getLoggedInUser(100);
    this.snackBar.open("Picture added", "Dismiss", {duration: 2000});
  }

  pickMainPicture(pictureId: number) {
    this.pictureService.pickMainPicture(pictureId).subscribe(
      (response: Picture) => {
        this.getMainPicture(this.loggedInUser!);
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    )
  }

  deletePicture(pictureId: number) {
    this.pictureService.deletePicture(pictureId).subscribe(
      (response: void) => {
        this.getLoggedInUser(0);
        this.snackBar.open("Picture deleted", "Dismiss", {duration: 2000});
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    )
  }
}
