import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router'; // <-- Adicionado RouterLink
import { PacienteService } from '../service/paciente.service';

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
  
  // NOVO: Propriedade usada no *ngIf do HTML para o menu de Admin
  isAdmin: boolean = false; 

  constructor(
    private pacienteService: PacienteService,
    private router: Router
  ) {}

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