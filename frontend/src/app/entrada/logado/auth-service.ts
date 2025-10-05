import { Injectable } from '@angular/core';
import { LoginResponse } from '../login/login-models';

@Injectable({
  providedIn: 'root'
})

export class AuthService {

  constructor() { }

  saveToken(loginResponse: LoginResponse): void {
    // Com o uso de cookies, não é necessário salvar o token manualmente
    //    localStorage.setItem('jwt_token', loginResponse.token);
    localStorage.setItem('token_created_at', loginResponse.createdAt.toString());
    localStorage.setItem('token_expires_in', (loginResponse.createdAt + loginResponse.expiresIn).toString());
    localStorage.setItem('user_id', loginResponse.userId);
    localStorage.setItem('user_roles', JSON.stringify(loginResponse.roles));
  }

  getRoles(): string[] {
    const roles = localStorage.getItem('user_roles');
    return roles ? JSON.parse(roles) : [];
  }

  // O armazenamento de token JWT no localStorage foi removido
  // devido ao uso de cookies para autenticação.
  // o Uso de localStorage pode expor o token a ataques XSS.
  // getToken(): string | null {
  //   return localStorage.getItem('jwt_token');
  // } 

  removeToken(): void {
//    localStorage.removeItem('jwt_token');
    localStorage.removeItem('token_created_at');
    localStorage.removeItem('token_expires_in');
    localStorage.removeItem('user_id');
    localStorage.removeItem('user_roles');
  } 


  isLoggedIn(): boolean {
    return localStorage.getItem('user_id') !== null;
  }


}


