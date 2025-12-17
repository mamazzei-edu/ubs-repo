import { Paciente } from './paciente.model';
import { User } from './user.model';

export enum StatusAgendamento {
  AGENDADO = 'AGENDADO',
  CONFIRMADO = 'CONFIRMADO',
  CANCELADO = 'CANCELADO',
  REALIZADO = 'REALIZADO',
  FALTOU = 'FALTOU'
}

export interface Agendamento {
  id: number;
  paciente: Paciente;
  medico: User;
  dataHoraConsulta: string;
  status: StatusAgendamento;
  tipoConsulta: string;
  observacoes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface AgendamentoRequest {
  pacienteId: number;
  medicoId: number;
  dataHoraConsulta: string;
  tipoConsulta: string;
  observacoes?: string;
}

export const TIPOS_CONSULTA = [
  'Consulta Cardiologia',
  'Consulta Dermatologia',
  'Consulta Endocrinologia',
  'Consulta Gastroenterologia',
  'Consulta Ginecologia',
  'Consulta Neurologia',
  'Consulta Oftalmologia',
  'Consulta Ortopedia',
  'Consulta Pediatria',
  'Consulta Psiquiatria',
  'Consulta Clínica Geral',
  'Exame de Rotina',
  'Consulta de Retorno'
];