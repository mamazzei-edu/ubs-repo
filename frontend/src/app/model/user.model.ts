import { Role } from "./role.model";

export class User {

    usuarioSelecionado?: User;

    public id?: string;
    public fullName: string = '';
    public matricula: string = '';
    public email: string = '';
    public username: string = '';
    public password: string = '';
    // Campos da função MEDICO. Vêm da entidade Medico, que é a
    // referenciada por Agendamento; o backend mantém os dois em dia.
    public crm?: string;
    public especialidade?: string;
    public telefone?: string;
    public ativo?: boolean;
    public role?: Role;
}