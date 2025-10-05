import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../service/user-service.service';
import { Role } from '../model/role.model';
import { RoleService } from '../service/role-service.service';

@Component({
  selector: 'app-user-cadastro',
  imports: [CommonModule, FormsModule],
  standalone: true,
  templateUrl: './user-cadastro.component.html',
  styleUrl: './user-cadastro.component.css',
    providers: [UserService, RoleService]
})
export class UserCadastroComponent implements OnInit {
  usuarios: any[] = [];
  roles: Role[] = [];
  mensagem: string = '';
  pesquisaId: string = '';
  usuarioSelecionado: any = null;
  mostrarModalEditar: boolean = false;
  mostrarModalCadastro: boolean = false;
  roleSelecionado?: Role;

  constructor(private usuarioservice: UserService, private roleService: RoleService) { }

  ngOnInit(): void {
    this.carregarUsuarios();
    this.carregarRoles();
  }

  carregarRoles(): void {
    this.roleService.listarRoles().subscribe({
      next: (dados) => {
        this.roles = dados;
      },
      error: () => {
        this.mensagem = 'Erro ao carregar a lista de funções.';
      },
    });
  }

  id:number = 0;
  fullName: string = '';
  matricula: string = '';
  email: string = '';
  username: string = '';
  password: string = '';
  crm: string = '';
  role: Role | null = null;
  
    carregarUsuarios(): void {
    this.usuarioservice.listarUsuarios().subscribe({
      next: (dados) => {
        this.usuarios = dados;
        this.mensagem = this.usuarios.length === 0 ? 'Nenhum funcionário encontrado.' : '';
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
      next: (usuario) => {
        this.usuarios = usuario ? [usuario] : [];
        this.mensagem = usuario ? '' : 'Nenhum funcionário encontrado com o ID fornecido.';
      },
      error: () => {
        this.mensagem = 'Erro ao buscar funcionário.';
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
    this.roleSelecionado = this.roles.find(role => role.id === this.usuarioSelecionado.role.id) || undefined;
    this.mostrarModalEditar = true;
  }

  abrirModalCadastro() : void{
    this.mostrarModalCadastro = true;
  }

  fecharModal(): void {
    this.mostrarModalCadastro = false;
    this.mostrarModalEditar = false;
    this.usuarioSelecionado = null;
  }

  atualizarUsuario(): void {
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

  salvarUsuario(){
    const usuario = {
      fullName: this.fullName,
      matricula: this.matricula,
      email: this.email,
      username: this.username,
      password: this.password,
      crm: this.crm,
      role: this.roleSelecionado ? this.roleSelecionado.id : null
    };

    this.usuarioservice.criarUsuario(usuario).subscribe({
      next: () => {
        alert('Usuário cadastrado!');
        window.location.reload();
      },
      error: () => {
        alert('deu erro ao tentar cadastrar usuário');
      }
    });

  }

}
