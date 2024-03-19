import { Injectable } from '@angular/core';
import { Match } from '../interfaces/match';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Message } from '../interfaces/message';

@Injectable({
  providedIn: 'root',
})
export class MatchService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  public getAllUserMatches(): Observable<Match[]> {
    return this.http.get<Match[]>(`${this.apiServerUrl}/match/all`, {
      withCredentials: true,
    });
  }

  public addMessage(message: string): Observable<Message> {
    return this.http.post<Message>(`${this.apiServerUrl}/message`, message, {
      withCredentials: true,
    });
  }

  public updateMessage(message: Message): Observable<Message> {
    return this.http.put<Message>(`${this.apiServerUrl}/message`, message, {
      withCredentials: true,
    });
  }

  public deleteMessage(messageId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiServerUrl}/message/${messageId}`, {
      withCredentials: true,
    });
  }
}
