export class Medico {
  public id?: number;
  public nomeCompleto: string = '';
  public especialidade: string = '';
  public crm: string = '';
  public email: string = '';
  public telefone?: string;
  public ativo?: boolean = true;
  public createdAt?: string;
  public updatedAt?: string;

  constructor(init?: Partial<Medico>) {
    Object.assign(this, init);
  }
}