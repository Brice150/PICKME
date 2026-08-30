import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AdminSearch } from '../interfaces/admin-search';
import { AdminStats } from '../interfaces/admin-stats';
import { DeletedAccount } from '../interfaces/deleted-account';
import { User } from '../interfaces/user';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly apiServerUrl = environment.apiBaseUrl;

  getAdminStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.apiServerUrl}/admin/stats`, {
      withCredentials: true,
    });
  }

  getAllUsers(adminSearch: AdminSearch, page: number): Observable<User[]> {
    return this.http.post<User[]>(
      `${this.apiServerUrl}/admin/user/all/${page}`,
      adminSearch,
      {
        withCredentials: true,
      }
    );
  }

  getAllDeletedAccounts(
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

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiServerUrl}/admin/${userId}`, {
      withCredentials: true,
    });
  }
}
