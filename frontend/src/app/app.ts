import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from './entrada/logado/auth-service';
import { WithCredentialsInterceptor } from './with-credentials-interceptor';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatToolbarModule, RouterLink, MatIconModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  authService: AuthService;
  constructor(authService: AuthService) {
    this.authService = authService;
  }
  protected readonly title = signal('frontend');
  logout() {
       this.authService.removeToken();
  }


}
