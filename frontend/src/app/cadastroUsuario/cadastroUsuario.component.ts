import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cadastro-usuario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cadastrousuario.component.html',
  styleUrls: ['./cadastrousuario.component.css']
})
export class CadastroUsuarioComponent {
  usuario = {
    nomeCompleto: '',
    email: '',
    senha: '',
    funcao: '',
    departamento: ''
  };

  errorMessage: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  cadastroUsuario() {
    this.errorMessage = '';
    this.http.post('http://localhost:8090/api/usuarios', this.usuario).subscribe({
      next: () => {
        alert('Usuário cadastrado com sucesso!');
        this.router.navigate(['/login']);
      },
      error: err => {
        console.error('Erro ao cadastrar:', err);
        this.errorMessage = 'Erro ao cadastrar usuário!';
      }
    });
  }

  voltar() {
    this.router.navigate(['/']);
  }
}
