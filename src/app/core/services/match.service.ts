import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Match } from '../interfaces/match';

@Injectable({ providedIn: 'root' })
export class MatchService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  public getAllMatches(): Observable<Match[]> {
    return this.http.get<Match[]>(`${this.apiServerUrl}/match/all`, {
      withCredentials: true,
    });
  }
}
