import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router'; // <-- Adicionado RouterLink
import { PacienteService } from '../service/paciente.service';
import { mensagemDoErro } from '../core/api';
import { cpfValido, mascararCpf } from '../core/cpf';

@Component({
  selector: 'app-lista',
  templateUrl: './lista.component.html',
  styleUrls: ['./lista.component.css'],
  standalone: true,
  // CORREÇÃO: Adicionando RouterLink nos imports para que os botões funcionem
  imports: [CommonModule, FormsModule, RouterLink], 
})
export class ListaComponent implements OnInit {
  pacientes: any[] = [];
  pesquisaId: string = '';
  mensagem: string = '';
  pacienteSelecionado: any = null;
  mostrarModalEditar: boolean = false;
  userRole: string = '';
  cpfInvalido: boolean = false;
  erroEdicao: string = '';
  
  // NOVO: Propriedade usada no *ngIf do HTML para o menu de Admin
  isAdmin: boolean = false; 

  constructor(
    private pacienteService: PacienteService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // 1. Carrega o papel do usuário
    this.userRole = localStorage.getItem('role') || '';
    
    // 2. Define a variável de controle para o *ngIf do menu de Admin
    // Inclui ADMIN e SUPER_ADMIN, já que ambos são administradores
    this.isAdmin = this.userRole === 'ADMIN' || this.userRole === 'SUPER_ADMIN'; 

    // 3. Carrega os pacientes (funcionalidade principal da tela). Vindo do
    //    aviso de CPF duplicado do cadastro (?id=), já abre aquele paciente.
    const idInformado = this.route.snapshot.queryParamMap.get('id');
    if (idInformado) {
      this.pesquisaId = idInformado;
      this.pesquisarPacientePorId();
    } else {
      this.carregarPacientes();
    }
  }

  formatarCpf(cpf: string | null | undefined): string {
    return mascararCpf(cpf);
  }

  aoDigitarCpf(valor: string): void {
    this.pacienteSelecionado.cpf = mascararCpf(valor);
    this.cpfInvalido = false;
  }
  
  // NOVO: Função para verificar se o usuário é MEDICO/USER, se necessário
  // Você pode usar isso para esconder os botões 'Editar' e 'Excluir' da tabela, se quiser.
  hasAccess(roles: string[]): boolean {
    return roles.includes(this.userRole);
  }


  carregarPacientes(): void {
    this.pacienteService.listarPacientes().subscribe({
      next: (dados) => {
        this.pacientes = dados;
        this.mensagem =
          this.pacientes.length === 0
            ? 'Nenhum paciente encontrado.'
            : '';
      },
      error: () => {
        this.mensagem = 'Erro ao carregar a lista de pacientes.';
      },
    });
  }

  pesquisarPacientePorId(): void {
    if (!this.pesquisaId) {
      this.carregarPacientes();
      return;
    }
    this.pacienteService.buscarPacientePorId(this.pesquisaId).subscribe({
      next: (paciente) => {
        // CORREÇÃO: Garante que 'pacientes' seja um array para o *ngFor
        this.pacientes = paciente ? [paciente] : []; 
        this.mensagem = paciente
          ? ''
          : 'Nenhum paciente encontrado com o ID fornecido.';
      },
      error: () => {
        this.mensagem = 'Erro ao buscar paciente.';
      },
    });
  }

  excluirPaciente(id: string): void {
    if (confirm('Tem certeza que deseja excluir este paciente?')) {
      this.pacienteService.excluirPaciente(id).subscribe({
        next: () => {
          this.carregarPacientes();
          this.mensagem = 'Paciente excluído com sucesso.';
        },
        error: () => {
          this.mensagem = 'Erro ao excluir paciente.';
        },
      });
    }
  }

  abrirModalEditar(paciente: any): void {
    this.pacienteSelecionado = { ...paciente, cpf: mascararCpf(paciente.cpf) };
    this.cpfInvalido = false;
    this.erroEdicao = '';
    this.mostrarModalEditar = true;
  }

  fecharModal(): void {
    this.mostrarModalEditar = false;
    this.pacienteSelecionado = null;
  }

  atualizarPaciente(): void {
    this.erroEdicao = '';
    if (!cpfValido(this.pacienteSelecionado.cpf)) {
      this.cpfInvalido = true;
      this.erroEdicao = 'Informe um CPF válido.';
      return;
    }
    this.pacienteService
      .editarPaciente(this.pacienteSelecionado.id, this.pacienteSelecionado)
      .subscribe({
        next: () => {
          this.fecharModal();
          this.carregarPacientes();
        },
        error: (err) => {
          // 409 = o CPF digitado pertence a outro paciente; 400 = CPF inválido.
          // O erro fica dentro do modal, que continua aberto para correção.
          this.erroEdicao = mensagemDoErro(err, 'Erro ao atualizar paciente.');
        },
      });
  }

  // 🚪 Função para logout
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.router.navigate(['/login']);
  }
}