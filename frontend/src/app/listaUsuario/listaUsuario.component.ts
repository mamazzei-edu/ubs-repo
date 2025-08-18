import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { User } from '../model/user.model';  
import { LoginService } from '../service/login.service'; 

@Component({
  selector: 'app-lista-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './lista-usuarios.component.html',
  styleUrls: ['./lista-usuarios.component.css'],
  providers: [LoginService]
})
export class ListaUsuariosComponent {
  mensagem: string = "";
  usuarios: User[] = [];
  usuarioSelecionado: User | null = null;  

  constructor(private service: LoginService, private http: HttpClient) { 
    this.listar();  
  }

  listar() {
    this.service.listar().subscribe({
      next: (data: User[]) => { this.usuarios = data; },
      error: () => { this.mensagem = "Ocorreu um erro ao carregar os usuários."; }
    });
  }

  editar(id: number) {
    this.service.buscarPorId(id).subscribe({
      next: (usuario: User) => {
        this.usuarioSelecionado = { ...usuario };
        // Por segurança, não trazer a senha do backend
        this.usuarioSelecionado.senha = '';
      },
      error: (erro: any) => {
        console.error('Erro ao carregar os dados do usuário:', erro);
      }
    });
  }

  salvarEdicao(usuario: User) {
    if (!usuario || !usuario.id) {
      console.error("Usuário inválido!", usuario);
      return;
    }

    this.http.put(`http://localhost:8090/api/usuarios/${usuario.id}`, usuario)
    .subscribe({
        next: () => {
          this.mensagem = 'Usuário atualizado com sucesso!';
          this.usuarioSelecionado = null;
          this.listar();
        },
        error: (err: any) => {
          console.error('Erro ao atualizar usuário:', err);
          this.mensagem = 'Erro ao atualizar usuário.';
        }
      });
  }

  cancelarEdicao() {
    this.usuarioSelecionado = null;  
  }

  remover(id: number) {
    if (!id) {
      console.error('ID inválido para remoção');
      return;
    }
  
    if (confirm('Tem certeza que deseja remover este usuário?')) {
      this.service.remover(id).subscribe({
        next: (response: { mensagem: string; }) => {
          this.mensagem = response.mensagem || 'Usuário removido com sucesso';
          this.listar(); // Atualiza a lista após remoção
        },
        error: (erro: any) => {
          console.error('Erro ao remover usuário:', erro);
          this.mensagem = 'Erro ao remover usuário';
        }
      });
    }
  }
}
