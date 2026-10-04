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

  // Campos do modal de cadastro
  id: number = 0;
  nomeCompleto: string = '';
  matricula: string = '';
  email: string = '';
  nomeUsuario: string = '';
  password: string = '';

  // Específicos da função MEDICO
  crm: string = '';
  especialidade: string = '';
  telefone: string = '';

  role: Role | null = null;

  /**
   * A função selecionada é MEDICO?
   *
   * Controla a exibição de CRM, Especialidade e Telefone nos dois modais, e é
   * o que o backend usa para criar/atualizar o registro na tabela "medico" —
   * a entidade que o Agendamento referencia.
   */
  get medicoSelecionado(): boolean {
    return this.roleSelecionado?.name === 'MEDICO';
  }

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
    this.roleSelecionado = this.roles.find(role => role.id === this.usuarioSelecionado.role?.id) || undefined;
    // A senha fica em branco: deixar assim mantém a atual (ver UserService).
    this.usuarioSelecionado.password = '';
    this.mostrarModalEditar = true;
  }

  abrirModalCadastro(): void {
    // Limpa o formulário: sem isto, o modal reabria com o que sobrou do anterior.
    this.nomeCompleto = '';
    this.matricula = '';
    this.email = '';
    this.nomeUsuario = '';
    this.password = '';
    this.crm = '';
    this.especialidade = '';
    this.telefone = '';
    this.roleSelecionado = undefined;
    this.mostrarModalCadastro = true;
  }

  fecharModal(): void {
    this.mostrarModalCadastro = false;
    this.mostrarModalEditar = false;
    this.usuarioSelecionado = null;
  }

  atualizarUsuario(): void {
    const usuario = {
      ...this.usuarioSelecionado,
      // O rádio altera roleSelecionado; sem copiar para o objeto enviado, a
      // troca de função nunca chegava ao backend.
      role: this.roleSelecionado ?? null,
      crm: this.medicoSelecionado ? this.usuarioSelecionado.crm : null,
      especialidade: this.medicoSelecionado ? this.usuarioSelecionado.especialidade : null,
      telefone: this.medicoSelecionado ? this.usuarioSelecionado.telefone : null,
    };

    this.usuarioservice.editarUsuario(this.usuarioSelecionado.id, usuario).subscribe({
      next: () => {
        this.fecharModal();
        this.carregarUsuarios();
      },
      error: (erro) => {
        this.mensagem = this.mensagemDeErro(erro, 'Erro ao atualizar usuario.');
      }
    });
  }

  salvarUsuario(): void {
    const usuario = {
      nomeCompleto: this.nomeCompleto,
      matricula: this.matricula,
      email: this.email,
      nomeUsuario: this.nomeUsuario,
      password: this.password,
      // Objeto inteiro, e não só o id: o backend desserializa em Role.
      role: this.roleSelecionado ?? null,
      crm: this.medicoSelecionado ? this.crm : null,
      especialidade: this.medicoSelecionado ? this.especialidade : null,
      telefone: this.medicoSelecionado ? this.telefone : null,
    };

    this.usuarioservice.criarUsuario(usuario).subscribe({
      next: () => {
        this.fecharModal();
        this.carregarUsuarios();
        this.mensagem = 'Usuário cadastrado com sucesso.';
      },
      error: (erro) => {
        this.mensagem = this.mensagemDeErro(erro, 'Erro ao cadastrar usuário.');
      }
    });
  }

  /** O backend devolve ProblemDetail; o campo detail traz a causa real. */
  private mensagemDeErro(erro: any, padrao: string): string {
    return erro?.error?.detail || padrao;
  }
}
