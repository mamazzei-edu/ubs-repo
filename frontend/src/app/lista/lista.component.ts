import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';  // 🔹 Importação necessária para [(ngModel)]
import { Paciente } from '../model/paciente';
import { PacienteService } from '../service/paciente.service';

@Component({
  selector: 'app-lista',
  standalone: true,
  imports: [CommonModule, FormsModule],  // 🔹 Adicionado FormsModule
  templateUrl: './lista.component.html',
  styleUrls: ['./lista.component.css'],
  providers: [PacienteService]
})
export class ListaComponent {
  mensagem: string = "";
  pacientes: Paciente[] = [];
  pacienteSelecionado: Paciente | null = null;  // 🔹 Armazena o paciente em edição
  Paciente!: number;

  constructor(private service: PacienteService) {
    this.listar();
  }

  listar() {
    this.service.listar().subscribe({
      next: (data) => { this.pacientes = data; },
      error: () => { this.mensagem = "Ocorreu um erro ao carregar os pacientes."; }
    });
  }

  // 🔹 Método para buscar e editar paciente
  editar(codigo: number) {
    this.service.buscarPorCodigo(codigo).subscribe({
      next: (paciente: Paciente) => {
        console.log('Paciente encontrado:', paciente);
        this.pacienteSelecionado = { ...paciente }; // 🔹 Removeu id desnecessário
      },
      error: (erro) => {
        console.error('Erro ao carregar os dados do paciente:', erro);
      }
    });
  }
  
  
// Método para salvar as edições
salvarEdicao() {
  if (this.pacienteSelecionado) {
    this.service.updatePaciente(this.pacienteSelecionado.codigo, this.pacienteSelecionado).subscribe({
      next: () => {
        console.log(`Paciente ${this.pacienteSelecionado?.codigo} atualizado com sucesso`);
        this.mensagem = 'Paciente atualizado com sucesso!';
        this.pacienteSelecionado = null; // Fecha o formulário de edição
        this.listar();  // Atualiza a lista com os novos dados
      },
      error: () => {
        this.mensagem = "Erro ao atualizar o paciente.";
      }
    });
  }
}


  // 🔹 Método para cancelar a edição
  cancelarEdicao() {
    this.pacienteSelecionado = null;  // Fecha o painel de edição sem salvar
  }

  // 🔹 Método para remover paciente
  remover(codigo: number) {
    if (!codigo) {
      console.error('Código inválido para remoção');
      return;
    }

    if (confirm('Tem certeza que deseja remover este paciente?')) {
      this.service.remover(codigo).subscribe({
        next: () => {
          console.log(`Paciente ${codigo} removido com sucesso`);
          this.mensagem = 'Paciente removido com sucesso!';
          this.listar();  // Atualiza a lista após remoção
        },
        error: (erro) => {
          console.error('Erro ao remover paciente:', erro);
          this.mensagem = 'Erro ao remover paciente';
        }
      });
    }
  }
}
