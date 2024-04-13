import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../interfaces/user';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { AdminSearch } from '../interfaces/admin-search';
import { DeletedAccount } from '../interfaces/deleted-account';
import { AdminStats } from '../interfaces/admin-stats';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private apiServerUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  public getAdminStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.apiServerUrl}/admin/stats`, {
      withCredentials: true,
    });
  }

  public getAllUsers(
    adminSearch: AdminSearch,
    page: number
  ): Observable<User[]> {
    return this.http.post<User[]>(
      `${this.apiServerUrl}/admin/user/all/${page}`,
      adminSearch,
      {
        withCredentials: true,
      }
    );
  }

  public getAllDeletedAccounts(
    adminSearch: AdminSearch,
    page: number
  ): Observable<DeletedAccount[]> {
    return this.http.post<DeletedAccount[]>(
      `${this.apiServerUrl}/admin/deleted-account/all/${page}`,
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
