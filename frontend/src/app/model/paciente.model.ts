export class Paciente {
 
  // Informações pessoais
  pacienteSelecionado?: Paciente;

  public codigo?: number; // Código do paciente
  public nomeCompleto: string = ''; // Nome completo
  public nomeSocial?: string; // Nome social (opcional)
  public nomeMae?: string; // Nome da mãe (opcional)
  public nomePai?: string; // Nome do pai (opcional)

  // Dados de nascimento
  public dataNascimento: Date = new Date(); // Data de nascimento
  public sexo: 'Masculino' | 'Feminino' | 'Outro' = 'Masculino'; // Sexo

  // Localidade
  public nacionalidade: string = ''; // Nacionalidade
  public municipioNascimento: string = ''; // Município de nascimento

  // Outros dados
  public racaCor: 'Branca' | 'Preta' | 'Parda' | 'Amarela' | 'Indígena' = 'Branca'; // Raça/Cor
  public frequentaEscola?: 'Sim' | 'Não';
  public estabelecimentoVeiculo?: string; 
  public estabelecimentoCadastro?: string; 
  

  // Dados de deficiência
  public deficiente?: 'Sim' | 'Não'; // Se o paciente possui deficiência
  public visual?: 'Sim' | 'Não'; // Deficiência visual
  public auditiva?: 'Sim' | 'Não'; // Deficiência auditiva
  public motora?: 'Sim' | 'Não'; // Deficiência motora
  public intelectual?: 'Sim' | 'Não'; // Deficiência intelectual

  // Dados de contato
  
    public telefoneCelular: string = '';  
    public telefoneResidencial?: string; 
    public telefoneComercial?: string;   
    public email: string = '';       
    public cpf: string = '';            
  
  

  
  constructor(init?: Partial<Paciente>) {
    Object.assign(this, init); 
  }
}
