import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PacienteService } from '../service/paciente.service';

@Component({
  selector: 'app-lista',
  templateUrl: './lista.component.html',
  styleUrls: ['./lista.component.css'],
  standalone: true,
  // CORREÇÃO: Adicionando RouterLink nos imports para que os botões funcionem
  imports: [CommonModule, FormsModule],
})
export class ListaComponent implements OnInit {
  pacientes: any[] = [];
  termoPesquisa: string = '';
  mensagem: string = '';
  pacienteSelecionado: any = null;
  mostrarModalEditar: boolean = false;
  userRole: string = '';

  // NOVO: Propriedade usada no *ngIf do HTML para o menu de Admin
  isAdmin: boolean = false;

  // Paginação
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  maxVisiblePages: number = 5;

  constructor(
    private pacienteService: PacienteService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // 1. Carrega o papel do usuário
    this.userRole = localStorage.getItem('role') || '';

    // 2. Define a variável de controle para o *ngIf do menu de Admin
    // Inclui ADMIN e SUPER_ADMIN, já que ambos são administradores
    this.isAdmin = this.userRole === 'ADMIN' || this.userRole === 'SUPER_ADMIN';

    // 3. Carrega os pacientes (funcionalidade principal da tela)
    this.carregarPacientes();
  }

  // NOVO: Função para verificar se o usuário é MEDICO/USER, se necessário
  // Você pode usar isso para esconder os botões 'Editar' e 'Excluir' da tabela, se quiser.
  hasAccess(roles: string[]): boolean {
    return roles.includes(this.userRole);
  }


  carregarPacientes(): void {
    this.pacienteService.listarPacientesPaginados(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.pacientes = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
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

  proximaPagina(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.carregarPacientes();
    }
  }

  paginaAnterior(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.carregarPacientes();
    }
  }

  irParaPagina(page: number): void {
    this.currentPage = page;
    this.carregarPacientes();
  }

  getVisiblePages(): number[] {
    const pages: number[] = [];
    let startPage = Math.max(0, this.currentPage - Math.floor(this.maxVisiblePages / 2));
    let endPage = Math.min(this.totalPages - 1, startPage + this.maxVisiblePages - 1);

    // Ajustar se não houver páginas suficientes antes
    if (endPage - startPage + 1 < this.maxVisiblePages) {
      startPage = Math.max(0, endPage - this.maxVisiblePages + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  }

  pesquisarPaciente(): void {
    if (!this.termoPesquisa) {
      this.carregarPacientes();
      return;
    }

    const termo = this.termoPesquisa.trim();

    // Remove caracteres não numéricos para verificar quantidade de dígitos
    const digitos = termo.replace(/\D/g, '');
    // Remove caracteres não alfabéticos para verificar quantidade de letras
    const letras = termo.replace(/[^a-zA-Z]/g, '');

    // Verifica se é busca por CPF (pelo menos 2 números)
    if (digitos.length >= 2) {
      this.pacienteService.buscarPorCpfParcial(digitos).subscribe({
        next: (pacientes) => {
          this.pacientes = pacientes;
          this.totalElements = pacientes.length;
          this.totalPages = 1; // Desativa paginação para busca filtrada
          this.currentPage = 0;
          this.mensagem = pacientes.length === 0
            ? 'Nenhum paciente encontrado por CPF.'
            : '';
        },
        error: () => {
          this.mensagem = 'Erro ao buscar por CPF.';
        }
      });
    }
    // Verifica se é busca por Nome (pelo menos 2 letras)
    else if (letras.length >= 2) {
      this.pacienteService.buscarPorNomeParcial(termo).subscribe({
        next: (pacientes) => {
          this.pacientes = pacientes;
          this.totalElements = pacientes.length;
          this.totalPages = 1; // Desativa paginação
          this.currentPage = 0;
          this.mensagem = pacientes.length === 0
            ? 'Nenhum paciente encontrado por Nome.'
            : '';
        },
        error: () => {
          this.mensagem = 'Erro ao buscar por Nome.';
        }
      });
    }
    else {
      this.mensagem = 'Digite pelo menos 2 letras para nome ou 2 números para CPF.';
    }
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
    this.pacienteSelecionado = { ...paciente };
    this.mostrarModalEditar = true;
  }

  fecharModal(): void {
    this.mostrarModalEditar = false;
    this.pacienteSelecionado = null;
  }

  atualizarPaciente(): void {
    this.pacienteService
      .editarPaciente(this.pacienteSelecionado.id, this.pacienteSelecionado)
      .subscribe({
        next: () => {
          this.fecharModal();
          this.carregarPacientes();
        },
        error: () => {
          this.mensagem = 'Erro ao atualizar paciente.';
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
