import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor() {}

  // Função para fazer login
  login(username: string, password: string): boolean {
    if (username === 'admin' && password === 'senha') {
      sessionStorage.setItem('isLoggedIn', 'true');
      return true;
    }
    return false;
  }

  // Função para verificar se o usuário está logado
  isLoggedIn(): boolean {
    return sessionStorage.getItem('isLoggedIn') === 'true';
  }

  // Função para fazer logout
  logout() {
    sessionStorage.removeItem('isLoggedIn');
  }
}
