import { Injectable } from '@angular/core';
import { LoginCliente } from './login-cliente';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginResponse } from './login-models';


@Injectable({
  providedIn: 'root'
})

export class LoginService {
  private apiUrl = 'http://localhost:8090/auth/login';
  constructor(private http: HttpClient) { }

  logar(loginCliente: LoginCliente): Observable<LoginResponse> {
    console.log('Tentando logar com:', JSON.stringify(loginCliente) );
    return this.http.post<LoginResponse>(this.apiUrl, loginCliente);
  }


  login(loginCliente: LoginCliente) {
    console.log('Login realizado com sucesso:', loginCliente);
  }
}
