import { User } from './user.model';

export interface Medico extends User {
  crm: string;
  especialidades: string[]; //As especialidades obrigatóriamente devem ser um array de strings
}