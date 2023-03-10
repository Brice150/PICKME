import { HttpClient, HttpEvent} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Picture } from '../interfaces/picture';

@Injectable({providedIn: 'root'})
export class PictureService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) {}

    public getAllUserPictures(fkUser: number): Observable<Picture[]> {
        return this.http.get<Picture[]>(`${this.apiServerUrl}/picture/all/${fkUser}`,
        { withCredentials: true });
    }

    public getPicture(content: string): Observable<HttpEvent<Blob>> {
        return this.http.get(`${this.apiServerUrl}/picture/${content}`, {
            reportProgress: true,
            observe: 'events',
            responseType: 'blob',
            withCredentials: true
        })
    }

    public addPicture(formData: FormData): Observable<HttpEvent<string[]>> {
        return this.http.post<string[]>(`${this.apiServerUrl}/picture`, formData, {
            reportProgress: true,
            observe: 'events',
            withCredentials: true
        })
    }

    public pickMainPicture(pictureId: number): Observable<Picture> {
        return this.http.put<Picture>(`${this.apiServerUrl}/picture/${pictureId}`,
        { withCredentials: true });
    }

    public deletePicture(pictureId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/picture/${pictureId}`,
        { withCredentials: true });
    }
}