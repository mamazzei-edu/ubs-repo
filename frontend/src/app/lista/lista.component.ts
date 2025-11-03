import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { PacienteService } from '../service/paciente.service';

declare var $: any; // necessário para usar o DataTables com jQuery

@Component({
  selector: 'app-lista',
  templateUrl: './lista.component.html',
  styleUrls: ['./lista.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
})
export class ListaComponent implements OnInit, AfterViewInit, OnDestroy {
  pesquisaId: string = '';
  mensagem: string = '';
  userRole: string = '';
  isAdmin: boolean = false;
  tabela: any;
  pacienteSelecionado: any = null;
  mostrarModalEditar: boolean = false;

  constructor(
    private pacienteService: PacienteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userRole = localStorage.getItem('role') || '';
    this.isAdmin = this.userRole === 'ADMIN' || this.userRole === 'SUPER_ADMIN';
  }

  ngAfterViewInit(): void {
    this.inicializarTabela();
  }

  ngOnDestroy(): void {
    if (this.tabela) {
      this.tabela.destroy(true);
    }
  }

  inicializarTabela(): void {
    this.tabela = $('#tabelaPacientes').DataTable({
      pagingType: 'full_numbers',
      pageLength: 10,
      serverSide: true,
      processing: true,
      ajax: (dataTablesParameters: any, callback: any) => {
        // Se o usuário digitou um ID, busca apenas esse paciente
        if (this.pesquisaId.trim()) {
          this.pacienteService.buscarPacientePorId(this.pesquisaId).subscribe({
            next: (paciente) => {
              callback({
                recordsTotal: 1,
                recordsFiltered: 1,
                data: [paciente],
              });
            },
            error: () => {
              callback({
                recordsTotal: 0,
                recordsFiltered: 0,
                data: [],
              });
              this.mensagem = 'Paciente não encontrado.';
            },
          });
        } else {
          // Caso contrário, lista normal com paginação
          this.pacienteService
            .listarPacientesAjax(dataTablesParameters)
            .subscribe({
              next: (resp) => {
                callback({
                  recordsTotal: resp.recordsTotal,
                  recordsFiltered: resp.recordsFiltered,
                  data: resp.data,
                });
              },
              error: () => {
                this.mensagem = 'Erro ao carregar pacientes.';
              },
            });
        }
      },
      columns: [
        { title: 'Código', data: 'codigo' },
        { title: 'Nome Completo', data: 'nomeCompleto' },
        { title: 'Data de Nascimento', data: 'dataNascimento' },
        { title: 'Sexo', data: 'sexo' },
        { title: 'Contato', data: 'contatoCelular' },
        {
          title: 'Ações',
          data: null,
          orderable: false,
          render: (data: any) => {
            return `
              <button class="btn btn-sm btn-primary editar" data-id="${data.codigo}">Editar</button>
              <button class="btn btn-sm btn-danger excluir" data-id="${data.codigo}">Excluir</button>
            `;
          },
        },
      ],
      language: {
        url: '//cdn.datatables.net/plug-ins/1.10.25/i18n/Portuguese-Brasil.json',
      },
      destroy: true, // permite reinicializar a tabela ao recarregar
    });

    // Eventos de ação nos botões (usando arrow function para preservar o "this")
    $('#tabelaPacientes tbody').on('click', 'button.editar', (event: any) => {
      const id = $(event.currentTarget).data('id');
      this.abrirModalEditar(id);
    });

    $('#tabelaPacientes tbody').on('click', 'button.excluir', (event: any) => {
      const id = $(event.currentTarget).data('id');
      this.excluirPaciente(id);
    });
  }

  // 🔍 Pesquisa por ID (botão “Pesquisar” do HTML)
  pesquisarPacientePorId(): void {
    this.mensagem = '';
    if (this.tabela) {
      this.tabela.ajax.reload(); // recarrega a tabela com o filtro aplicado
    }
  }

  // ✏️ Abrir modal de edição
  abrirModalEditar(id: number): void {
    this.pacienteService.buscarPacientePorId(id.toString()).subscribe({
      next: (paciente) => {
        this.pacienteSelecionado = paciente;
        this.mostrarModalEditar = true;
      },
      error: () => {
        this.mensagem = 'Erro ao buscar paciente para edição.';
      },
    });
  }

  fecharModal(): void {
    this.mostrarModalEditar = false;
    this.pacienteSelecionado = null;
  }

  atualizarPaciente(): void {
    if (!this.pacienteSelecionado) return;
    this.pacienteService
      .editarPaciente(this.pacienteSelecionado.codigo, this.pacienteSelecionado)
      .subscribe({
        next: () => {
          this.fecharModal();
          this.tabela.ajax.reload();
          this.mensagem = 'Paciente atualizado com sucesso.';
        },
        error: () => {
          this.mensagem = 'Erro ao atualizar paciente.';
        },
      });
  }

  excluirPaciente(id: string): void {
    if (confirm('Tem certeza que deseja excluir este paciente?')) {
      this.pacienteService.excluirPaciente(id).subscribe({
        next: () => {
          this.tabela.ajax.reload();
          this.mensagem = 'Paciente excluído com sucesso.';
        },
        error: () => {
          this.mensagem = 'Erro ao excluir paciente.';
        },
      });
    }
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.router.navigate(['/login']);
  }
}
