import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../interfaces/user';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private apiServerUrl = environment.apiBaseUrl;
  users: User[] = [
    {
      id: 1,
      nickname: 'Kate',
      email: 'kate@gmail.com',
      userRole: 'HIDDEN',
      gender: 'Woman',
      genderSearch: 'Man',
      birthDate: new Date('1995-06-08T22:00:00.000+00:00'),
      city: 'Rennes',
      height: 170,
      job: 'Lawyer',
      minAge: 18,
      maxAge: 30,
      description:
        'I’m looking for a great guy with a good sense of humor. I also am a very long description to tell everyone about me and to test the app. What are you waiting for ?',
      mainPicture: 'https://picsum.photos/1000/1000?random=' + 1,
      smokes: 'Never smokes',
      alcoholDrinking: 'Drinks sometimes alcohol',
      organised: 'Very organised',
      personality: 'Ambivert',
      sportPractice: 'Athlete',
      animals: 'Likes animals',
      parenthood: 'Will want children someday',
      gamer: 'Play video games sometimes',
      gold: true,
      totalDislikes: 3,
      totalLikes: 20,
      totalMatches: 2,
      pictures: [
        {
          id: 11,
          content: 'https://picsum.photos/1000/1000?random=' + 1,
          isMainPicture: true,
        },
        {
          id: 203,
          content: 'https://picsum.photos/1000/1000?random=' + 18,
          isMainPicture: false,
        },
        {
          id: 205,
          content: 'https://picsum.photos/1000/1000?random=' + 22,
          isMainPicture: false,
        },
        {
          id: 206,
          content: 'https://picsum.photos/1000/1000?random=' + 23,
          isMainPicture: false,
        },
      ],
    },
    {
      id: 2,
      nickname: 'Julia',
      email: 'julia@gmail.com',
      userRole: 'HIDDEN',
      gender: 'Woman',
      genderSearch: 'Man',
      birthDate: new Date('1998-07-12T22:00:00.000+00:00'),
      city: 'Rennes',
      height: 174,
      job: 'Journalist',
      minAge: 18,
      maxAge: 30,
      mainPicture: 'https://picsum.photos/1000/1000?random=' + 2,
      description: 'I’m looking for a great guy with a good sense of humor',
      smokes: 'Smokes sometimes',
      alcoholDrinking: 'Never drinks alcohol',
      organised: 'Very organised',
      personality: 'Ambivert',
      sportPractice: 'Athlete',
      animals: 'Likes animals',
      parenthood: 'Will want children someday',
      gamer: 'Play video games sometimes',
      totalDislikes: 19,
      totalLikes: 6,
      totalMatches: 3,
      pictures: [
        {
          id: 12,
          content: 'https://picsum.photos/1000/1000?random=' + 2,
          isMainPicture: true,
        },
        {
          id: 204,
          content: 'https://picsum.photos/1000/1000?random=' + 19,
          isMainPicture: false,
        },
      ],
    },
    {
      id: 3,
      nickname: 'Alexandra',
      email: 'alexandra@gmail.com',
      userRole: 'HIDDEN',
      gender: 'Woman',
      genderSearch: 'Man',
      job: 'Student',
      birthDate: new Date('1990-01-09T23:00:00.000+00:00'),
      city: 'Rennes',
      minAge: 18,
      maxAge: 30,
      mainPicture: 'https://picsum.photos/1000/1000?random=' + 3,
      totalDislikes: 2,
      totalLikes: 3,
      totalMatches: 1,
      pictures: [
        {
          id: 3,
          content: 'https://picsum.photos/1000/1000?random=' + 3,
          isMainPicture: true,
        },
      ],
    },
    {
      id: 4,
      nickname: 'Jesta',
      email: 'jesta@gmail.com',
      userRole: 'HIDDEN',
      gender: 'Woman',
      genderSearch: 'Man',
      job: 'Mother',
      birthDate: new Date('1997-07-07T22:00:00.000+00:00'),
      city: 'Rennes',
      minAge: 18,
      maxAge: 30,
      totalDislikes: 6,
      totalLikes: 15,
      totalMatches: 1,
      pictures: [],
    },
    {
      id: 5,
      nickname: 'Natasha',
      email: 'natasha@gmail.com',
      userRole: 'HIDDEN',
      gender: 'Woman',
      genderSearch: 'Man',
      job: 'Cashier',
      birthDate: new Date('2003-06-11T22:00:00.000+00:00'),
      city: 'Rennes',
      minAge: 18,
      maxAge: 30,
      mainPicture: 'https://picsum.photos/1000/1000?random=' + 5,
      totalDislikes: 1,
      totalLikes: 10,
      totalMatches: 9,
      pictures: [
        {
          id: 14,
          content: 'https://picsum.photos/1000/1000?random=' + 5,
          isMainPicture: true,
        },
      ],
    },
    {
      id: 6,
      nickname: 'Sophie',
      email: 'sophie@gmail.com',
      userRole: 'HIDDEN',
      gender: 'Woman',
      genderSearch: 'Man',
      job: 'Unemployed',
      birthDate: new Date('1996-07-15T22:00:00.000+00:00'),
      city: 'Rennes',
      minAge: 18,
      maxAge: 30,
      mainPicture: 'https://picsum.photos/1000/1000?random=' + 6,
      totalDislikes: 6,
      totalLikes: 2,
      totalMatches: 0,
      pictures: [
        {
          id: 15,
          content: 'https://picsum.photos/1000/1000?random=' + 6,
          isMainPicture: true,
        },
      ],
    },
  ];

  constructor(private http: HttpClient) {}

  public getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiServerUrl}/admin/user`, {
      withCredentials: true,
    });
  }

  public deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiServerUrl}/admin/user/${userId}`, {
      withCredentials: true,
    });
  }
}
