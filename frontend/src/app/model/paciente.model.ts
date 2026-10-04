// Espelha a entidade Paciente do backend: mesmas propriedades, mesma ordem.
// Alterou a entidade? Alterar aqui, em cadastro.component.ts/.html e em
// upload.component.html — as quatro listas precisam continuar iguais.
export class Paciente {

  public id?: number;

  // Informações pessoais
  public nomeCompleto: string = '';
  public nomeSocial?: string;
  public nomeMae?: string;
  public nomePai?: string;
  public dataNascimento: string = '';
  public sexo?: 'Masculino' | 'Feminino' | 'Outro';
  public cpf: string = '';
  public cns?: string;
  public prontuario?: string;
  public nacionalidade?: string;
  public municipioNascimento?: string;
  public racaCor?: 'Branca' | 'Preta' | 'Parda' | 'Amarela' | 'Indígena';
  public etnia?: string;

  // Dados educacionais e sociais
  public frequentaEscola?: 'Sim' | 'Não';
  public escolaridade?: string;
  public situacaoFamiliar?: string;
  public ocupacao?: string;

  // Deficiências e OPM
  public deficiente?: 'Sim' | 'Não';
  public visual?: 'Sim' | 'Não';
  public auditiva?: 'Sim' | 'Não';
  public motora?: 'Sim' | 'Não';
  public intelectual?: 'Sim' | 'Não';
  public opm?: 'Sim' | 'Não';

  // Contatos
  public telefoneCelular: string = '';
  public telefoneResidencial?: string;
  public telefoneComercial?: string;
  public email: string = '';
  public contato?: string;

  // Endereço
  public cep?: string;
  public logradouro?: string;
  public numero?: string;
  public bairro?: string;
  public complemento?: string;
  public uf?: string;
  public municipioResidencia?: string;
  public distritoAdministrativo?: string;
  public tipoLogradouro?: string;
  public origemEndereco?: string;
  public referencia?: string;

  // Documentos
  public rg?: string;
  public orgaoEmissor?: string;
  public pisPasepNis?: string;
  public cnh?: string;
  public ctps?: string;
  public tituloEleitor?: string;
  public passaporte?: string;

  // Vínculos e cadastro
  public estabelecimentoVinculo?: string;
  public estabelecimentoCadastro?: string;

  constructor(init?: Partial<Paciente>) {
    Object.assign(this, init);
  }
}
