
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  FilaEsperaApiService,
  PacienteFila
} from '../service/fila-espera.service';

import { PacienteService } from '../service/paciente.service';

@Component({
  selector: 'app-fila-espera',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './fila-espera.component.html',
  styleUrls: ['./fila-espera.component.scss']
})
export class FilaEsperaComponent implements OnInit {

  pacientes: PacienteFila[] = [];
  fila: PacienteFila[] = [];

  pacienteSelecionadoId: number | null = null;

  mensagem = '';
  tipoMensagem = '';

  constructor(
    private filaService: FilaEsperaApiService,
    private pacienteService: PacienteService
  ) {}

  ngOnInit(): void {
    this.carregarPacientes();
    this.carregarFila();
  }

  carregarPacientes(): void {
    this.pacienteService.listarPacientes().subscribe({
      next: (dados) => {
        this.pacientes = dados;
      },
      error: () => {
        this.mostrarMensagem(
          'Não foi possível carregar os pacientes cadastrados.',
          'erro'
        );
      }
    });
  }

  carregarFila(): void {
    this.filaService.listarFila().subscribe({
      next: (dados) => {
        this.fila = dados;
      },
      error: () => {
        this.mostrarMensagem(
          'Não foi possível carregar a fila de espera.',
          'erro'
        );
      }
    });
  }

  adicionarPaciente(): void {
    if (this.pacienteSelecionadoId === null) {
      this.mostrarMensagem(
        'Selecione um paciente antes de adicionar.',
        'erro'
      );
      return;
    }

    this.filaService
      .adicionarPaciente(this.pacienteSelecionadoId)
      .subscribe({
        next: () => {
          this.mostrarMensagem(
            'Paciente adicionado à fila com sucesso!',
            'sucesso'
          );

          this.pacienteSelecionadoId = null;
          this.carregarFila();
        },
        error: (erro) => {
          const mensagem = erro.status === 409
            ? 'Este paciente já está na fila.'
            : erro.status === 404
              ? 'Paciente não encontrado.'
              : 'Erro ao adicionar paciente à fila.';

          this.mostrarMensagem(mensagem, 'erro');
        }
      });
  }

  chamarProximo(): void {
    if (this.fila.length === 0) {
      this.mostrarMensagem(
        'Não existem pacientes aguardando atendimento.',
        'erro'
      );
      return;
    }

    this.filaService.chamarProximo().subscribe({
      next: (paciente) => {
        if (paciente) {
          this.mostrarMensagem(
            `Paciente ${paciente.nomeCompleto} chamado para atendimento.`,
            'sucesso'
          );
        }

        this.carregarFila();
      },
      error: () => {
        this.mostrarMensagem(
          'Não foi possível chamar o próximo paciente.',
          'erro'
        );
      }
    });
  }

  removerPaciente(id: number): void {
    this.filaService.removerPaciente(id).subscribe({
      next: () => {
        this.mostrarMensagem(
          'Paciente removido da fila.',
          'sucesso'
        );

        this.carregarFila();
      },
      error: () => {
        this.mostrarMensagem(
          'Não foi possível remover o paciente.',
          'erro'
        );
      }
    });
  }

  private mostrarMensagem(
    texto: string,
    tipo: string
  ): void {
    this.mensagem = texto;
    this.tipoMensagem = tipo;
  }
}
