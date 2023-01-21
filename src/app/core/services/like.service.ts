import { HttpClient} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Like } from '../interfaces/like';

@Injectable({providedIn: 'root'})
export class LikeService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) {}

    public getLikeByFk(fkSender: number, fkReceiver: number): Observable<Like> {
        return this.http.get<Like>(`${this.apiServerUrl}/like/${fkSender}/${fkReceiver}`,
        { withCredentials: true });
    }

    public addLike(like: Like): Observable<Like> {
        return this.http.post<Like>(`${this.apiServerUrl}/like`, like,
        { withCredentials: true });
    }

    public deleteLike(likeId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/like/${likeId}`,
        { withCredentials: true });
    }
}