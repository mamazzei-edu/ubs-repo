export class User {
  usuarioSelecionado?: User;

  public matricula: string = '';
  public email: string = '';
  public senha: string = '';
  public funcao: string = '';
    id: any;

  constructor(init?: Partial<User>) {
    Object.assign(this, init);
  }
}
