import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Paciente } from '../model/paciente.model';
import { PacienteService } from '../service/paciente.service';

@Component({
  selector: 'app-lista',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './lista.component.html',
  styleUrls: ['./lista.component.css'],
  providers: [PacienteService]
})
export class ListaComponent {
  mensagem: string = "";
  pacientes: Paciente[] = [];
  pacienteSelecionado: Paciente | null = null;  

  constructor(private service: PacienteService) {
    this.listar();
  }

  listar() {
    this.service.listar().subscribe({
      next: (data) => { this.pacientes = data; },
      error: () => { this.mensagem = "Ocorreu um erro ao carregar os pacientes."; }
    });
  }

  editar(codigo: number) {
    this.service.buscarPorCodigo(codigo).subscribe({
      next: (paciente: Paciente) => {
        console.log('Paciente encontrado:', paciente);
        this.pacienteSelecionado = { ...paciente, codigo: paciente.codigo ?? 0 }; 
      },
      error: (erro) => {
        console.error('Erro ao carregar os dados do paciente:', erro);
      }
    });
  }

  salvarEdicao() {
    if (this.pacienteSelecionado) {
      const codigoPaciente = this.pacienteSelecionado.codigo ?? 0;
  
      this.service.updatePaciente(codigoPaciente, this.pacienteSelecionado).subscribe({
        next: (response: { mensagem: string; }) => {  
          console.log(response.mensagem);
          this.mensagem = response.mensagem; 
          this.pacienteSelecionado = null;
          this.listar();
        },
        error: () => {
          this.mensagem = "Erro ao atualizar o paciente.";
        }
      });
    }
  }
  

  cancelarEdicao() {
    this.pacienteSelecionado = null;  
  }

  remover(codigo: number) {
    if (!codigo) {
      console.error('Código inválido para remoção');
      return;
    }
  
    if (confirm('Tem certeza que deseja remover este paciente?')) {
      this.service.remover(codigo).subscribe({
        next: (response) => {  // response agora é do tipo { mensagem: string }
          console.log(response.mensagem);  // Exibe a mensagem retornada pela API
          this.mensagem = response.mensagem;  // Atualiza a mensagem na interface
          this.listar();
        },
        error: (erro) => {
          console.error('Erro ao remover paciente:', erro);
          this.mensagem = 'Erro ao remover paciente';
        }
      });
    }
  }
  
  }
  
  
  
  

    
  
