import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Paciente } from '../model/paciente';
import { PacienteService } from '../service/paciente.service';  // Certifique-se de que o serviço está correto

@Component({
  selector: 'app-lista',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './lista.component.html',
  styleUrls: ['./lista.component.css'],
  providers: [PacienteService]
})
export class ListaComponent {
  mensagem: string = "";
  pacientes: Paciente[] = [];
  pacienteEdicao!: Paciente;

  constructor(private service: PacienteService) {
    this.listar();
  }

  listar() {
    this.service.listar().subscribe({
      next: (data) => { this.pacientes = data; },
      error: (msg) => { this.mensagem = "Ocorreu um erro"; }
    });
  }

 // Método de edição
 editar(codigo: number) {
  if (codigo === 0) {
    console.error('Código inválido');
    return;
  }

  this.service.buscarPorCodigo(codigo).subscribe({
    next: (paciente: Paciente) => {
      // Aqui você pode configurar a lógica de exibição dos dados do paciente
      // Exemplo: redirecionando para um formulário de edição ou exibindo um modal.
      console.log('Paciente para editar:', paciente);
      this.pacienteEdicao = paciente; // Armazenando o paciente para edição, se necessário
    },
    error: () => {
      this.mensagem = 'Erro ao carregar os dados do paciente';
    }
  });
}


// Método de remoção
remover(codigo: number) {
  if (confirm('Tem certeza que deseja remover este paciente?')) {
    this.service.remover(codigo).subscribe({
      next: () => {
        this.mensagem = 'Paciente removido com sucesso!';
        this.listar();  // Atualiza a lista após remoção
      },
      error: () => {
        this.mensagem = 'Erro ao remover paciente';
      }
    });
  }
}

        }
    