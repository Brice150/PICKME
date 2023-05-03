import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
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
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, OnDestroy{
  imagePath: string = environment.imagePath;
  loggedInUser!: User | null;
  loggedInUserEmail!: string | null;
  updateForm!: FormGroup;
  pictures: Picture[] = [];
  picturesData!: FormData;
  selectedPictureId!: number;
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
  getLoggedInUserSubscription!: Subscription;
  updateUserSubscription!: Subscription;
  deleteUserSubscription!: Subscription;
  getMainPictureSubscription!: Subscription;
  getAllUserPicturesSubscription!: Subscription;
  getPictureSubscription!: Subscription;
  addPictureSubscription!: Subscription;
  deletePictureSubscription!: Subscription;

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

  ngOnDestroy() {
    this.getLoggedInUserSubscription && this.getLoggedInUserSubscription.unsubscribe();
    this.updateUserSubscription && this.updateUserSubscription.unsubscribe();
    this.deleteUserSubscription && this.deleteUserSubscription.unsubscribe();
    this.getMainPictureSubscription && this.getMainPictureSubscription.unsubscribe();
    this.getAllUserPicturesSubscription && this.getAllUserPicturesSubscription.unsubscribe();
    this.getPictureSubscription && this.getPictureSubscription.unsubscribe();
    this.addPictureSubscription && this.addPictureSubscription.unsubscribe();
    this.deletePictureSubscription && this.deletePictureSubscription.unsubscribe();
  }

  getLoggedInUser(wait: number) {
    setTimeout(() => {
      this.getLoggedInUserSubscription = this.userService.getConnectedUser().subscribe({
        next: (response: User) => {
          this.loggedInUser = response;
          this.getMainPicture(this.loggedInUser);
          this.getPictures(this.loggedInUser.id);
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, "Server error", {
            positionClass: "toast-bottom-center" 
          })
        }
      })
    }, wait)
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
    if (this.selectedPictureId) {
      user.mainPicture = this.selectedPictureId.toString();
    }
    this.updateUserSubscription = this.userService.updateUser(user).subscribe({
      next: (response: User) => {
        this.updateForm.get("password")?.reset();
        this.getLoggedInUser(0);
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      },
      complete: () => {
        this.toastr.success("Profile updated", "Profile", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
  }

  deleteUser() {
    this.deleteUserSubscription = this.userService.deleteConnectedUser().subscribe({
      next: (response: void) => {
        sessionStorage.removeItem('loggedInUserEmail');
        this.router.navigate(['/connect'])
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      },
      complete: () => {
        this.toastr.success("Profile deleted", "Profile", {
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
    const loaderWrapper = document.getElementById('loaderWrapper');
    loaderWrapper!.style.display = 'none';
  }

  getPictures(fkUser: any) {
    this.pictures = [];
    this.getAllUserPicturesSubscription = this.pictureService.getAllUserPictures(fkUser).subscribe({
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
  }

  addPicture(files: File[]) {
    this.picturesData = new FormData();
    for (const file of files) {
      this.picturesData.append('content', file, file.name);
    }
    this.addPictureSubscription = this.pictureService.addPicture(this.picturesData).subscribe({
      next: event => {
        console.log(event);
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
    this.getLoggedInUser(100);
    this.toastr.success("Picture added", "Profile", {
      positionClass: "toast-bottom-center" 
    })
  }

  pickMainPicture(picture: Picture) {
    this.selectedPictureId = picture.id;
    this.loggedInUser!.mainPicture = picture.content;
    this.toastr.info("Please type password bottom page to update profile", "Profile", {
      positionClass: "toast-bottom-center" 
    })
    this.toastr.success("Main picture selected", "Profile", {
      positionClass: "toast-bottom-center" 
    })
  }

  deletePicture(pictureId: number) {
    this.deletePictureSubscription = this.pictureService.deletePicture(pictureId).subscribe({
      next: (response: void) => {
        this.getLoggedInUser(0);
      },
      error: (error: HttpErrorResponse) => {
        this.toastr.error(error.message, "Server error", {
          positionClass: "toast-bottom-center" 
        })
      },
      complete: () => {
        this.toastr.success("Picture deleted", "Profile", {
          positionClass: "toast-bottom-center" 
        })
      }
    })
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
