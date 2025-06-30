import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../service/user-service.service';

@Component({
  selector: 'app-user-cadastro',
  imports: [CommonModule, FormsModule],
  standalone: true,
  templateUrl: './user-cadastro.component.html',
  styleUrl: './user-cadastro.component.css',
    providers: [UserService]
})
export class UserCadastroComponent implements OnInit {
  usuarios: any[] = [];
  mensagem: string = '';
  pesquisaId: string = '';
  usuarioSelecionado: any = null;
    mostrarModalEditar: boolean = false;
  
  constructor(private usuarioservice : UserService){}
  
  ngOnInit(): void {
    this.carregarUsuarios();
  }

    carregarUsuarios(): void {
    this.usuarioservice.listarUsuarios().subscribe({
      next: (dados) => {
        this.usuarios = dados;
        this.mensagem = this.usuarios.length === 0 ? 'Nenhum paciente encontrado.' : '';
      },
      error: () => {
        this.mensagem = 'Erro ao carregar a lista de usuários.';
      },
    });
  }

    pesquisarUsuarioPorId(): void {
    if (!this.pesquisaId) {
      this.carregarUsuarios();
      return;
    }
    this.usuarioservice.buscarUsuarioPorId(this.pesquisaId).subscribe({
      next: (paciente) => {
        this.usuarios = paciente ? [paciente] : [];
        this.mensagem = paciente ? '' : 'Nenhum paciente encontrado com o ID fornecido.';
      },
      error: () => {
        this.mensagem = 'Erro ao buscar paciente.';
      },
    });
  }

    excluirUsuario(id: string): void {
    if (confirm('Tem certeza que deseja excluir este Usuario?')) {
      this.usuarioservice.excluirUsuario(id).subscribe({
        next: () => {
          this.carregarUsuarios();
          this.mensagem = 'Usuario excluído com sucesso.';
        },
        error: () => {
          this.mensagem = 'Erro ao excluir usuario.';
        },
      });
    }
  }

    abrirModalEditar(usuario: any): void {
    this.usuarioSelecionado = { ...usuario };
    this.mostrarModalEditar = true;
  }

  fecharModal(): void {
    this.mostrarModalEditar = false;
    this.usuarioSelecionado = null;
  }

  atualizarPaciente(): void {
    this.usuarioservice.editarUsuario(this.usuarioSelecionado.id, this.usuarioSelecionado).subscribe({
      next: () => {
        this.fecharModal();
        this.carregarUsuarios();
      },
      error: () => {
        this.mensagem = 'Erro ao atualizar usuario.';
      }
    });
  }

}
