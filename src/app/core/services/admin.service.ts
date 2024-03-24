import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../interfaces/user';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { AdminSearch } from '../interfaces/admin-search';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  public getAllUsers(
    adminSearch: AdminSearch,
    page: number
  ): Observable<User[]> {
    return this.http.post<User[]>(
      `${this.apiServerUrl}/admin/all/${page}`,
      adminSearch,
      {
        withCredentials: true,
      }
    );
  }

  public deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiServerUrl}/admin/${userId}`, {
      withCredentials: true,
    });
  }
}
