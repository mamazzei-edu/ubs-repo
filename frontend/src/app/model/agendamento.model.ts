import { Paciente } from './paciente.model';
import { Medico } from './medico.model';

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
  medico: Medico;
  dataHoraConsulta: string;
  status: StatusAgendamento;
  tipoConsulta: string;
  /** false quando a hora foi deixada em branco no cadastro. Vem do backend. */
  horarioDefinido?: boolean;
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