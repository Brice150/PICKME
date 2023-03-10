import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { DialogComponent } from '../dialog/dialog.component';
import { Picture } from '../core/interfaces/picture';
import { User } from '../core/interfaces/user';
import { PictureService } from '../core/services/picture.service';
import { UserService } from '../core/services/user.service';
import { ToastrService } from 'ngx-toastr';

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
  alcoholDrinking: string[] = [
    "Never drinks alcohol",
    "Drinks sometimes alcohol",
    "Drinks a lot alcohol"
  ];
  smokes: string[] = [
    "Never smokes",
    "Smokes sometimes",
    "Smokes a lot"
  ];
  sportPractice: string[] = [
    "Never practice sport",
    "Practices sport sometimes",
    "Athlete"
  ];
  parenthood: string[] = [
    "Doesn't want children",
    "Will want children someday",
    "Has children"
  ];
  gamer: string[] = [
    "Never play video games",
    "Play video games sometimes",
    "Play video games a lot"
  ];
  animals: string[] = [
    "Doesn't like animals",
    "Likes animals",
    "Has animals"
  ];
  organised: string[] = [
    "Messy",
    "Reasonably organised",
    "Very organised"
  ];
  personality: string[] = [
    "Introvert",
    "Ambivert",
    "Extravert"
  ];

  constructor(
    private router: Router,
    private userService: UserService,
    private pictureService: PictureService,
    private fb: FormBuilder,
    private toastr: ToastrService,
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
          this.toastr.error(error.message, "Server error", {
            positionClass: "toast-bottom-center" 
          });
        }
      )
    }, wait);
  }

  updateUser(user: User) {
    user.alcoholDrinking = this.loggedInUser!.alcoholDrinking;
    user.smokes = this.loggedInUser!.smokes;
    user.sportPractice = this.loggedInUser!.sportPractice;
    user.parenthood = this.loggedInUser!.parenthood;
    user.gamer = this.loggedInUser!.gamer;
    user.animals = this.loggedInUser!.animals;
    user.organised = this.loggedInUser!.organised;
    user.personality = this.loggedInUser!.personality;
    this.userService.updateUser(user).subscribe(
      (response: User) => {
        this.updateForm.get("password")?.reset();
        this.toastr.success("Profile updated", "Profile", {
          positionClass: "toast-bottom-center" 
        });
      },
      (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        });
      }
    );
  }

  deleteUser() {
    this.userService.deleteUser(this.loggedInUser?.email!).subscribe(
      (response: void) => {
        sessionStorage.removeItem('loggedInUserEmail');
        this.router.navigate(['/connect'])
        .then(() => {
          this.toastr.success("Profile deleted", "Profile", {
            positionClass: "toast-bottom-center" 
          });
        });
      },
      (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        });
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
    const loaderWrapper = document.getElementById('loaderWrapper');
    loaderWrapper!.style.display = 'none';
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
                this.toastr.error(error.message, "Server error", {
                  positionClass: "toast-bottom-center" 
                });
              }
            );   
            this.pictures.push(picture);
          }   
          }
        },
      (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        });
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
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        });
      }
    );
    this.getLoggedInUser(100);
    this.toastr.success("Picture added", "Profile", {
      positionClass: "toast-bottom-center" 
    });
  }

  pickMainPicture(pictureId: number) {
    this.pictureService.pickMainPicture(pictureId).subscribe(
      (response: Picture) => {
        this.getMainPicture(this.loggedInUser!);
        this.toastr.success("Main picture selected", "Profile", {
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

  deletePicture(pictureId: number) {
    this.pictureService.deletePicture(pictureId).subscribe(
      (response: void) => {
        this.getLoggedInUser(0);
        this.toastr.success("Picture deleted", "Profile", {
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

  isSelected(attribute: string, property: string): boolean {
    let selected: boolean = false;
    if (attribute && this.loggedInUser) {
      selected = (this.loggedInUser as any)[property] === attribute
    }
    return selected;
  }

  select(attribute: string, property: string) {
    if (attribute && this.loggedInUser) {
      (this.loggedInUser as any)[property] = attribute;
    }
  }
}
