import { Role } from "./role.model";

export class User {

    usuarioSelecionado?: User;

    public id?: string;
    public fullName: string = '';
    public matricula: string = '';
    public email: string = '';
    public username: string = '';
    public password: string = '';
    public crm?: string;
    public role?: Role;
}