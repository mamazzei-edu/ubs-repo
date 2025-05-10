import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PacienteService } from '../service/paciente.service';

@Component({
  selector: 'app-lista',
  templateUrl: './lista.component.html',
  styleUrls: ['./lista.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [PacienteService]
})
export class ListaComponent implements OnInit {
  pacientes: any[] = [];
  pesquisaId: string = '';
  mensagem: string = '';
  pacienteSelecionado: any = null;
  mostrarModalEditar: boolean = false;

  constructor(private pacienteService: PacienteService) {}

  ngOnInit(): void {
    this.carregarPacientes();
  }

  carregarPacientes(): void {
    this.pacienteService.listarPacientes().subscribe({
      next: (dados) => {
        this.pacientes = dados;
        this.mensagem = this.pacientes.length === 0 ? 'Nenhum paciente encontrado.' : '';
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
        this.pacientes = paciente ? [paciente] : [];
        this.mensagem = paciente ? '' : 'Nenhum paciente encontrado com o ID fornecido.';
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
    console.log('Paciente selecionado:', paciente);  // Verifique os dados no console
    this.pacienteSelecionado = { ...paciente };  // Cria uma cópia do paciente para edição
    this.mostrarModalEditar = true;
  }

  fecharModal(): void {
    this.mostrarModalEditar = false;
    this.pacienteSelecionado = null;
  }

  atualizarPaciente(): void {
    console.log('Paciente a ser atualizado:', this.pacienteSelecionado);  // Verifique se o objeto está correto
    this.pacienteService.editarPaciente(this.pacienteSelecionado.id, this.pacienteSelecionado).subscribe({
      next: (paciente) => {
        console.log('Paciente atualizado com sucesso:', paciente);
        this.fecharModal();  // Fecha a modal após atualização
        this.carregarPacientes();  // Atualiza a lista de pacientes
      },
      error: (err) => {
        console.error('Erro ao atualizar paciente:', err);
        this.mensagem = 'Erro ao atualizar paciente.';
      }
    });
  }
}
