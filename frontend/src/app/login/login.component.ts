import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service'; // Importação do serviço de autenticação

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
voltar() {
throw new Error('Method not implemented.');
}
  username: string = ''; // Nome de usuário
  password: string = ''; // Senha
  errorMessage: string = ''; // Mensagem de erro caso o login falhe

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    // Chama o método login do AuthService
    if (this.authService.login(this.username, this.password)) {
      // Se o login for bem-sucedido, redireciona para a página principal
      this.router.navigate(['/lista']);
    } else {
      // Se falhar, exibe a mensagem de erro
      this.errorMessage = 'Usuário ou senha inválidos!';
    }
  }
}
