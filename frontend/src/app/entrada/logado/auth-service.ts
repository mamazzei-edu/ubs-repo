import { Injectable } from '@angular/core';
import { LoginResponse } from '../login/login-models';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor() {}

  /**
   * Salva os dados do login localmente (exceto o token JWT, que é armazenado via cookie)
   */
  saveToken(loginResponse: LoginResponse): void {
    const createdAt = Number(loginResponse.createdAt);
    const expiresIn = Number(loginResponse.expiresIn);

    // Armazena metadados de sessão
    localStorage.setItem('token_created_at', createdAt.toString());
    localStorage.setItem('token_expires_in', (createdAt + expiresIn).toString());
    localStorage.setItem('user_id', loginResponse.userId.toString());
    localStorage.setItem('user_roles', JSON.stringify(loginResponse.roles || []));
  }

  /**
   * Retorna as roles (perfis) do usuário logado
   */
  getRoles(): string[] {
    const roles = localStorage.getItem('user_roles');
    try {
      return roles ? JSON.parse(roles) : [];
    } catch {
      return [];
    }
  }

  /**
   * Remove todos os dados de autenticação do localStorage
   */
  removeToken(): void {
    localStorage.removeItem('token_created_at');
    localStorage.removeItem('token_expires_in');
    localStorage.removeItem('user_id');
    localStorage.removeItem('user_roles');
  }

  /**
   * Verifica se o usuário está logado e se o token ainda é válido
   */
  isLoggedIn(): boolean {
    const userId = localStorage.getItem('user_id');
    const expiresIn = localStorage.getItem('token_expires_in');

    if (!userId || !expiresIn) return false;

    const now = Date.now();
    const expiration = Number(expiresIn);
    return now < expiration;
  }

  /**
   * Retorna o ID do usuário logado
   */
  getUserId(): string | null {
    return localStorage.getItem('user_id');
  }
}
