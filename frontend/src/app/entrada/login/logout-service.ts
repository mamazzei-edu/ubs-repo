import { Injectable, Inject } from '@angular/core';
import { LoginCliente } from './login-cliente';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginResponse } from './login-models';


@Injectable({
  providedIn: 'root'
})

export class LogoutService {
  private apiUrl = 'http://localhost:8090/auth/logout';
  constructor(@Inject(HttpClient) private http: HttpClient) { }

  logout(): Observable<LoginResponse> {
    console.log('Tentando logar com:', this.apiUrl);
    return this.http.post<LoginResponse>(this.apiUrl,"text");
  }

  logoutSuccess(loginCliente: LoginCliente) {
    console.log('Logout realizado com sucesso:', loginCliente);
  }
}
