import { Injectable } from '@angular/core';
import { LoginClienteCookie } from './login-cliente-cookie';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginResponseCookie } from './login-models-cookie';
import { environment } from '../../environment';


@Injectable({
  providedIn: 'root'
})

export class LoginServiceCookie {
  private apiUrl = environment.apiUrl + '/auth/logincookie';
  constructor(private http: HttpClient) { }

  logar(loginCliente: LoginClienteCookie): Observable<LoginResponseCookie> {
    console.log('Tentando logar com:', JSON.stringify(loginCliente));
    return this.http.post<LoginResponseCookie>(this.apiUrl, loginCliente);
  }


  login(loginCliente: LoginClienteCookie) {
    console.log('Login realizado com sucesso:', loginCliente);
  }
}
