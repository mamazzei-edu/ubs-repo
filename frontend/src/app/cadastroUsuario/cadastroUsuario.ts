import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cadastro-usuario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cadastroUsuario.html',
  styleUrls: ['./cadastroUsuario.css']
})

export class CadastroUsuario {
  usuario = {
    matricula: '',
    nomeCompleto: '',
    email: '',
    senha: '',
    funcao: '',
    departamento: '',
    username: ''
  };

  errorMessage = '';
  loading = false;

  constructor(private http: HttpClient, private router: Router) {}

  cadastroUsuario() {
    this.errorMessage = '';

    // ✅ Validação simples no frontend
    if (!this.usuario.nomeCompleto || !this.usuario.email || !this.usuario.senha) {
      this.errorMessage = 'Preencha todos os campos obrigatórios.';
      return;
    }

    this.loading = true;
    this.http.post('http://localhost:8090/api/usuarios', this.usuario).subscribe({
      next: () => {
        alert('Usuário cadastrado com sucesso!');
        this.router.navigate(['/login']);
      },
      error: err => {
        console.error('Erro ao cadastrar:', err);
        if (err.status === 400) {
          this.errorMessage = err.error?.message || 'Dados inválidos.';
        } else if (err.status === 409) {
          this.errorMessage = 'Este e-mail já está cadastrado.';
        } else {
          this.errorMessage = 'Erro ao cadastrar usuário. Tente novamente.';
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
