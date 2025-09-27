import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from './entrada/logado/auth-service';
import { CookieService } from 'ngx-cookie-service';
import { H } from '@angular/cdk/keycodes';
import { LogoutService } from './entrada/login/logout-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatToolbarModule, RouterLink, MatIconModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  authService: AuthService;
  loginCliente: any;
  logoutService: LogoutService;
  private cookieService: CookieService;
  constructor(authService: AuthService, logoutService: LogoutService, cookieService: CookieService) {
    this.authService = authService;
    this.logoutService = logoutService;
    this.cookieService = cookieService;
  }
  protected readonly title = signal('frontend');
  logout() {
    console.log('Realizando logout');
    this.logoutService.logout().subscribe({
      next: (response: any) => {
        console.log('Logout realizado com sucesso:', response);
        this.cookieService.delete('jwt', '/', 'localhost', true, 'Lax');
        this.authService.removeToken();
      },
      error: (error: any) => {
        console.error('Erro ao realizar logout:', error);
      }
    });
  }


}
