export class User{

      usuarioSelecionado?: User;

    public nomeCompleto: string = ''; 
    public matricula : string = '';
    public userName : string = '';
    public senha: string = '';
    public email : string = ' ';
    public cpf : string ='';
    

    constructor(init?: Partial<User>) {
    Object.assign(this, init); 
  }
    }
