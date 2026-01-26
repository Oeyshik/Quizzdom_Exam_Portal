import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import baseUrl from './helper';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  // Add user
  public addUser(user: any): Observable<any> {
    return this.http.post(`${baseUrl}/user/`, user);
  }

  // Check if username is taken
  public isUsernameTaken(username: string): Observable<boolean> {
    return this.http.get<boolean>(`${baseUrl}/user/check-username/${username}`);
  }

  // Request password reset
  public forgotPassword(username: string, email?: string): Observable<any> {
    return this.http.post(`${baseUrl}/password/forgot`, { username, email });
  }

  // Reset password with token
  public resetPassword(username: string, resetToken: string, newPassword: string): Observable<any> {
    return this.http.post(`${baseUrl}/password/reset`, {
      username,
      resetToken,
      newPassword
    });
  }
}
