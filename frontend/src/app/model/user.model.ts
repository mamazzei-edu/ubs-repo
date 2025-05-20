export class User{

      usuarioSelecionado?: User;

    public nomeUsuario: string = ''; 
    public senha: string = '';
    public email : string = ' ';
    public cpf : string ='';
    

    constructor(init?: Partial<User>) {
    Object.assign(this, init); 
  }
    }
