export class LoginCliente {
    email!: string;
    password!: string;

    static newLoginCliente() {
        const loginCliente = new LoginCliente();
        return loginCliente;
    }
}