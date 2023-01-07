import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from 'src/environments/environment';
import { User } from '../models/user';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-moreinfo',
  templateUrl: './moreinfo.component.html',
  styleUrls: ['./moreinfo.component.css']
})
export class MoreInfoComponent implements OnInit {
  imagePath: string = environment.imagePath;
  user!: User;

  constructor(
    private userService: UserService,
    private route: ActivatedRoute) {}

  ngOnInit() {
    this.getUser(this.route.snapshot.paramMap.get('id'));
  }

  getUser(id: any) {
    this.userService.findUserById(id!).subscribe(
      (response: User) => {
        this.user = response;
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    )
  }
}
