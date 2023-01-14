import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from 'src/environments/environment';
import { Picture } from '../models/picture';
import { User } from '../models/user';
import { PictureService } from '../services/picture.service';
import { UserService } from '../services/user.service';

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

  constructor(
    private userService: UserService,
    private pictureService: PictureService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
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
    this.getLoggedInUser();
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

  getLoggedInUser() {
    this.userService.getConnectedUser().subscribe(
      (response: User) => {
        this.loggedInUser = response;
        this.getMainPicture(response);
        this.getPictures(response.id);
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
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
    )
  }

  addPicture() {
    
  }
}
