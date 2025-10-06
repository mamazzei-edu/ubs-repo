export interface Agendamento {
  id?: number;
  paciente: {
    id: number;
    nomeCompleto: string;
    cpf: string;
    telefoneCelular: string;
    email: string;
  };
  medico: {
    id: number;
    fullName: string;
    crm: string;
    email: string;
  };
  dataHoraConsulta: string;
  status: StatusAgendamento;
  observacoes?: string;
  tipoConsulta: string;
  createdAt?: string;
  updatedAt?: string;
}

export enum StatusAgendamento {
  AGENDADO = 'AGENDADO',
  CONFIRMADO = 'CONFIRMADO',
  CANCELADO = 'CANCELADO',
  REALIZADO = 'REALIZADO',
  FALTOU = 'FALTOU'
}

export interface AgendamentoRequest {
  pacienteId: number;
  medicoId: number;
  dataHoraConsulta: string;
  tipoConsulta: string;
  observacoes?: string;
}

export const TIPOS_CONSULTA = [
  'Consulta Médica Geral',
  'Consulta Pediatria',
  'Consulta Ginecologia',
  'Consulta Cardiologia',
  'Consulta Dermatologia',
  'Consulta Psicologia',
  'Consulta Enfermagem',
  'Exame de Rotina',
  'Acompanhamento',
  'Retorno'
] as const;

export type TipoConsulta = typeof TIPOS_CONSULTA[number];