import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../interfaces/user';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ConnectService {
  private apiServerUrl = environment.apiBaseUrl;
  user: User = {
    id: 1,
    nickname: 'Brice',
    email: 'brice.lecomte0@gmail.com',
    userRole: 'ROLE_ADMIN',
    mainPicture: 'brice2.jpg',
    gender: 'Man',
    genderSearch: 'Woman',
    birthDate: new Date('1997-02-27T23:00:00.000+00:00'),
    city: 'Rennes',
    height: 184,
    job: 'Web Developer',
    minAge: 18,
    maxAge: 30,
    description: 'I am looking for a woman who is a nice person',
    smokes: 'Never smokes',
    alcoholDrinking: 'Drinks sometimes alcohol',
    organised: 'Very organised',
    personality: 'Ambivert',
    sportPractice: 'Athlete',
    animals: 'Likes animals',
    parenthood: 'Will want children someday',
    gamer: 'Play video games sometimes',
    pictures: [
      {
        id: 992,
        content: 'https://picsum.photos/1000/1000?random=' + 992,
        isMainPicture: true,
      },
      {
        id: 993,
        content: 'https://picsum.photos/1000/1000?random=' + 993,
        isMainPicture: false,
      },
      {
        id: 994,
        content: 'https://picsum.photos/1000/1000?random=' + 994,
        isMainPicture: false,
      },
      {
        id: 995,
        content: 'https://picsum.photos/1000/1000?random=' + 995,
        isMainPicture: false,
      },
    ],
  };

  connectedUser?: User = this.user;

  constructor(private http: HttpClient) {}

  public register(user: User): any {
    return this.http.post(`${this.apiServerUrl}/registration`, user, {
      withCredentials: true,
      responseType: 'text',
    });
  }

  public login(user: User): Observable<User> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + window.btoa(user.email + ':' + user.password),
    });
    return this.http.get<User>(`${this.apiServerUrl}/login`, {
      withCredentials: true,
      headers,
    });
  }
}
