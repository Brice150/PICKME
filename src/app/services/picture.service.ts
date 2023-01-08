import { HttpClient, HttpEvent} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Picture } from '../models/picture';

@Injectable({providedIn: 'root'})
export class PictureService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) {}

    public getPictures(id: number): Observable<Picture[]> {
        return this.http.get<Picture[]>(`${this.apiServerUrl}/picture/find/all/${id}`,
        { withCredentials: true });
    }

    public getPicture(content: string): Observable<HttpEvent<Blob>> {
        return this.http.get(`${this.apiServerUrl}/picture/get/${content}`, {
            reportProgress: true,
            observe: 'events',
            responseType: 'blob',
            withCredentials: true
        })
    }

    public addPicture(formData: FormData): Observable<HttpEvent<string[]>> {
        return this.http.post<string[]>(`${this.apiServerUrl}/picture/add`, formData, {
            reportProgress: true,
            observe: 'events',
            withCredentials: true
        })
    }

    public updatePicture(content: string): Observable<Picture> {
        return this.http.put<Picture>(`${this.apiServerUrl}/picture/update/${content}`,
        { withCredentials: true });
    }

    public deletePicture(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiServerUrl}/picture/delete/${id}`,
        { withCredentials: true });
    }
}