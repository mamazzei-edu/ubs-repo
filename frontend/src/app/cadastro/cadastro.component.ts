import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cadastro.component.html',
  styleUrls: ['./cadastro.component.css'],
})
export class CadastroComponent {
  
  nomeCompleto = '';
  nomeSocial = '';
  nomeMae = '';
  nomePai = '';

  
  dataNascimento = '';
  sexo = '';

  // Localidade
  nacionalidade = '';
  municipioNascimento = '';

  // Dados Complementares
  racaCor = '';
  frequentaEscola = '';
  escolaridade = '';
  situacaoFamiliar = '';
  vinculoEstabelecimento = '';
  deficiencia = '';

  // Dados de Contato
  contatoCelular = '';
  contatoResidencial = '';
  contatoComercial = '';
  contatoEmail = '';

  // Documentos
  cpf = '';
  identidade = '';
}
