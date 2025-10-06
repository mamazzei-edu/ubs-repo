import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule, MatCardHeader, MatCardTitle, MatCardContent, MatCardSubtitle } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatOptionModule } from '@angular/material/core';

import { AgendamentoService } from '../service/agendamento.service';
import { PacienteService } from '../service/paciente.service';
import { UserService } from '../service/user-service.service';
import { 
  Agendamento, 
  AgendamentoRequest, 
  StatusAgendamento, 
  TIPOS_CONSULTA 
} from '../model/agendamento.model';
import { Paciente } from '../model/paciente.model';
import { User } from '../model/user.model';

@Component({
  selector: 'app-agendamento',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatCardHeader,
    MatCardTitle,
    MatCardContent,
    MatCardSubtitle,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatOptionModule,
    MatButtonModule,
    MatTableModule,
    MatIconModule,
    MatChipsModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './agendamento.component.html',
  styleUrl: './agendamento.component.scss'
})
export class AgendamentoComponent implements OnInit {
  
  agendamentoForm: FormGroup;
  agendamentos: Agendamento[] = [];
  pacientes: any[] = [];
  medicos: any[] = [];
  
  tiposConsulta = TIPOS_CONSULTA;
  statusOptions = Object.values(StatusAgendamento);
  
  displayedColumns: string[] = [
    'paciente', 
    'medico', 
    'dataHora', 
    'tipoConsulta', 
    'status', 
    'acoes'
  ];

  constructor(
    private fb: FormBuilder,
    private agendamentoService: AgendamentoService,
    private pacienteService: PacienteService,
    private userService: UserService
  ) {
    this.agendamentoForm = this.fb.group({
      pacienteId: ['', Validators.required],
      medicoId: ['', Validators.required],
      dataConsulta: ['', Validators.required],
      horaConsulta: ['', Validators.required],
      tipoConsulta: ['', Validators.required],
      observacoes: ['']
    });
  }

  ngOnInit(): void {
    this.carregarDados();
  }

  carregarDados(): void {
    this.carregarAgendamentos();
    this.carregarPacientes();
    this.carregarMedicos();
  }

  carregarAgendamentos(): void {
    this.agendamentoService.listarTodos().subscribe({
      next: (agendamentos) => {
        this.agendamentos = agendamentos;
      },
      error: (error) => {
        console.error('Erro ao carregar agendamentos:', error);
        this.mostrarMensagem('Erro ao carregar agendamentos');
      }
    });
  }

  carregarPacientes(): void {
    this.pacienteService.listarPacientes().subscribe({
      next: (pacientes: any[]) => {
        this.pacientes = pacientes;
      },
      error: (error: any) => {
        console.error('Erro ao carregar pacientes:', error);
        this.mostrarMensagem('Erro ao carregar pacientes');
      }
    });
  }

  carregarMedicos(): void {
    this.userService.listarUsuarios().subscribe({
      next: (users: any[]) => {
        // Filtrar apenas usuários que são médicos (têm CRM)
        this.medicos = users.filter((user: any) => user.crm && user.crm.trim() !== '');
      },
      error: (error: any) => {
        console.error('Erro ao carregar médicos:', error);
        this.mostrarMensagem('Erro ao carregar médicos');
      }
    });
  }

  onSubmit(): void {
    if (this.agendamentoForm.valid) {
      const formValue = this.agendamentoForm.value;
      
      // Combinar data e hora
      const dataConsulta = new Date(formValue.dataConsulta);
      const [horas, minutos] = formValue.horaConsulta.split(':');
      dataConsulta.setHours(parseInt(horas), parseInt(minutos));

      const agendamentoRequest: AgendamentoRequest = {
        pacienteId: formValue.pacienteId,
        medicoId: formValue.medicoId,
        dataHoraConsulta: dataConsulta.toISOString(),
        tipoConsulta: formValue.tipoConsulta,
        observacoes: formValue.observacoes
      };

      this.agendamentoService.criarAgendamento(agendamentoRequest).subscribe({
        next: (agendamento) => {
          this.mostrarMensagem('Agendamento criado com sucesso!');
          this.agendamentoForm.reset();
          this.carregarAgendamentos();
        },
        error: (error) => {
          console.error('Erro ao criar agendamento:', error);
          this.mostrarMensagem('Erro ao criar agendamento. Verifique se o horário está disponível.');
        }
      });
    }
  }

  cancelarAgendamento(id: number): void {
    if (confirm('Tem certeza que deseja cancelar este agendamento?')) {
      this.agendamentoService.cancelarAgendamento(id).subscribe({
        next: () => {
          this.mostrarMensagem('Agendamento cancelado');
          this.carregarAgendamentos();
        },
        error: (error) => {
          console.error('Erro ao cancelar agendamento:', error);
          this.mostrarMensagem('Erro ao cancelar agendamento');
        }
      });
    }
  }

  confirmarAgendamento(id: number): void {
    this.agendamentoService.confirmarAgendamento(id).subscribe({
      next: () => {
        this.mostrarMensagem('Agendamento confirmado');
        this.carregarAgendamentos();
      },
      error: (error) => {
        console.error('Erro ao confirmar agendamento:', error);
        this.mostrarMensagem('Erro ao confirmar agendamento');
      }
    });
  }

  getStatusColor(status: StatusAgendamento): string {
    switch (status) {
      case StatusAgendamento.AGENDADO:
        return 'primary';
      case StatusAgendamento.CONFIRMADO:
        return 'accent';
      case StatusAgendamento.CANCELADO:
        return 'warn';
      case StatusAgendamento.REALIZADO:
        return 'primary';
      case StatusAgendamento.FALTOU:
        return 'warn';
      default:
        return 'primary';
    }
  }

  formatarDataHora(dataHora: string): string {
    return new Date(dataHora).toLocaleString('pt-BR');
  }

  private mostrarMensagem(mensagem: string): void {
    // Implementação simples sem MatSnackBar
    alert(mensagem);
  }
}
