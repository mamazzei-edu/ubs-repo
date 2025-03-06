import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http'; 
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

  constructor(private service: PacienteService, private http: HttpClient) { 
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

  salvarEdicao(paciente: Paciente) {
    if (!paciente || !paciente.codigo) {
      console.error("Paciente inválido!", paciente);
      return;
    }

    this.http.put(`http://localhost:8091/api/paciente/${paciente.codigo}`, paciente)
    .subscribe({
        next: () => console.log('Paciente atualizado com sucesso!'),
        error: (err: any) => console.error('Erro ao atualizar paciente:', err)
      });
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
        next: (response) => {
          console.log(response.mensagem);
          this.mensagem = response.mensagem || 'Paciente removido com sucesso';
          this.listar(); // Atualiza a lista de pacientes após a remoção
        },
        error: (erro) => {
          console.error('Erro ao remover paciente:', erro);
          this.mensagem = 'Erro ao remover paciente';
        }
      });
    }
  }
  
  }
