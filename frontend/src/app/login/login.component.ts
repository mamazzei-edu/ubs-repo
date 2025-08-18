import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login-usuario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  usuario = {
    email: '',
    senha: ''
  };

  errorMessage = '';
  loading = false;

  constructor(private http: HttpClient, private router: Router) {}

  login() {
    this.errorMessage = '';

    // ✅ Validação simples no frontend
    if ( !this.usuario.email || !this.usuario.senha) {
      this.errorMessage = 'Preencha todos os campos obrigatórios.';
      return;
    }

    this.loading = true;
    this.http.post('http://localhost:8090/login', this.usuario).subscribe({
      next: () => {
        alert('Usuário logado com sucesso!');
        this.router.navigate(['/']);
      },
      error: err => {
        console.error('Erro ao efetuar login:', err);
        if (err.status === 400) {
          this.errorMessage = err.error?.message || 'Dados inválidos.';
        } else if (err.status === 409) {
          this.errorMessage = 'Erro 1 de login.';
        } else {
          this.errorMessage = 'Erro efetuar o login. Tente novamente.';
        }
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  voltar() {
    this.router.navigate(['/']);
  }
}
