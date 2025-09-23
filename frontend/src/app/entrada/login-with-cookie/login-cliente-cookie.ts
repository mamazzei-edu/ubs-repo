export class LoginClienteCookie {
    email!: string;
    password!: string;

    static newLoginClienteCookie() {
        const loginCliente = new LoginClienteCookie();
        return loginCliente;
    }
}