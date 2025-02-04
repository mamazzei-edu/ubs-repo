import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router'; // Para funcionalidades de roteamento
import { PacienteService } from '../service/paciente.service';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, RouterModule], // Todos os imports adicionados
  templateUrl: './cadastro.component.html',
  styleUrls: ['./cadastro.component.css'],
  providers: [PacienteService]
})
export class CadastroComponent {
  
  nomeCompleto: string = '';
  nomeSocial: string = '';
  nomeMae: string = '';
  nomePai: string = '';
  dataNascimento: string = '';
  sexo: string = '';

  // Localidade
  nacionalidade: string = '';
  municipioNascimento: string = '';

  // Dados Complementares
  racaCor: string = '';
  frequentaEscola: string = '';
  escolaridade: string = '';
  situacaoFamiliar: string = '';
  vinculoEstabelecimento: string = '';
  deficiencia: string = '';

  // Dados de Contato
  contatoCelular: string = '';
  contatoResidencial: string = '';
  contatoComercial: string = '';
  contatoEmail: string = '';

  // Documentos
  cpf: string = '';

  // Variáveis para controle de mensagem de sucesso ou erro
  showModal: boolean = false;
  modalMessage: string = '';
  isModalVisible: boolean = false;

  constructor(private http: HttpClient) {}

  salvarPaciente() {
    const paciente = {
      nomeCompleto: this.nomeCompleto,
      nomeSocial: this.nomeSocial,
      nomeMae: this.nomeMae,
      nomePai: this.nomePai,
      dataNascimento: this.dataNascimento,
      sexo: this.sexo,
      nacionalidade: this.nacionalidade,
      municipioNascimento: this.municipioNascimento,
      racaCor: this.racaCor,
      frequentaEscola: this.frequentaEscola,
      escolaridade: this.escolaridade,
      situacaoFamiliar: this.situacaoFamiliar,
      vinculoEstabelecimento: this.vinculoEstabelecimento,
      deficiencia: this.deficiencia,
      contatoCelular: this.contatoCelular,
      contatoResidencial: this.contatoResidencial,
      contatoComercial: this.contatoComercial,
      contatoEmail: this.contatoEmail,
      cpf: this.cpf,
    };
    this.http.post('http://localhost:8090/api/pacientes', paciente).subscribe({
      next: (data) => {
        console.log('Paciente salvo com sucesso:', data);
        this.openModal(); // Abre a modal
      },
      error: (err) => {
        console.error('Erro ao salvar paciente:', err);
      },
    });
  }

  openModal() {
    this.isModalVisible = true;
  }

  closeModal() {
    this.isModalVisible = false;
  }
}
