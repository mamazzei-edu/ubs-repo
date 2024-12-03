export class Paciente {
    
    public codigo: number = 0;
 
    // Informações pessoais
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
    public frequentaEscola?: 'Sim' | 'Não'; // Frequenta escola (opcional)
 
    // Dados de contato
    public contatoCelular: string = ''; // Celular
    public contatoResidencial?: string; // Telefone residencial (opcional)
    public contatoEmail: string = ''; // Email
 
    constructor(init?: Partial<Paciente>) {
      Object.assign(this, init);
    }
  }
 
  // Exemplo de utilização
  const pacienteExemplo = new Paciente({
    codigo: 1,
    nomeCompleto: 'João Silva',
    dataNascimento: new Date('2000-01-01'),
    sexo: 'Masculino',
    nacionalidade: 'Brasileira',
    municipioNascimento: 'São Paulo',
    racaCor: 'Parda',
    contatoCelular: '11987654321',
    contatoEmail: 'joao.silva@example.com',
  })