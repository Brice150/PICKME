import { HttpClient} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { User } from '../../core/interfaces/user';

@Injectable({providedIn: 'root'})
export class AdminService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) {}

    public getAllUsers(): Observable<User[]> {
        return this.http.get<User[]>(`${this.apiServerUrl}/admin/user/all`,
        { withCredentials: true });
    }

    public deleteUser(email: string): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/admin/user/${email}`,
        { withCredentials: true });
    }

    public deleteMessage(messageId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/admin/message/${messageId}`,
        { withCredentials: true });
    }
}