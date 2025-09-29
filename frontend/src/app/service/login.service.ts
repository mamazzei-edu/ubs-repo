import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { shareReplay } from 'rxjs/operators';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root'  
})

export class LoginService {
  constructor(private http: HttpClient) {}

  private apiUrl = 'http://localhost:8090/login'; 

  login(email: string, password: string): Observable<User> {
    return this.http.post<User>(this.apiUrl, User);
  }
}
