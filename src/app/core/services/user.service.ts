import { HttpClient} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { User } from '../interfaces/user';

@Injectable({providedIn: 'root'})
export class UserService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) {}

    public getAllUsers(): Observable<User[]> {
        return this.http.get<User[]>(`${this.apiServerUrl}/user/all`,
        { withCredentials: true });
    }

    public getAllUsersThatLiked(): Observable<User[]> {
        return this.http.get<User[]>(`${this.apiServerUrl}/user/all/like`,
        { withCredentials: true });
    }

    public getAllUsersThatMatched(): Observable<User[]> {
        return this.http.get<User[]>(`${this.apiServerUrl}/user/all/match`,
        { withCredentials: true });
    }

    public getConnectedUser(): Observable<User> {
        return this.http.get<User>(`${this.apiServerUrl}/user`,
        { withCredentials: true });
    }
    
    public getUserById(userId: number): Observable<User> {
        return this.http.get<User>(`${this.apiServerUrl}/user/${userId}`,
        { withCredentials: true });
    }

    public updateUser(user: User): Observable<User> {
        return this.http.put<User>(`${this.apiServerUrl}/user`, user,
        { withCredentials: true });
    }

    public deleteUser(userEmail: string): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/user/${userEmail}`,
        { withCredentials: true });
    }
}