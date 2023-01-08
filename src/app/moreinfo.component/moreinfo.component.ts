import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from 'src/environments/environment';
import { Picture } from '../models/picture';
import { User } from '../models/user';
import { PictureService } from '../services/picture.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-moreinfo',
  templateUrl: './moreinfo.component.html',
  styleUrls: ['./moreinfo.component.css']
})
export class MoreInfoComponent implements OnInit {
  imagePath: string = environment.imagePath;
  user!: User;
  pictures: Picture[] = [];

  constructor(
    private userService: UserService,
    private pictureService: PictureService,
    private route: ActivatedRoute) {}

  ngOnInit() {
    this.getUser(this.route.snapshot.paramMap.get('id'));
  }

  getUser(id: any) {
    this.userService.findUserById(id).subscribe(
      (response: User) => {
        this.user = response;
        this.getPictures(id!);
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    )
  }

  getPictures(id: any) {
    this.pictureService.getPictures(id).subscribe(
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

  like(user: User) {
    
  }

  return () {
    
  }
}
