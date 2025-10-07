import { Component } from '@angular/core';
import { PacienteService } from '../service/paciente.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { RouterModule, Router } from '@angular/router'; // ✅ IMPORTAÇÃO DO ROUTER ADICIONADA

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
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

  nacionalidade: string = '';
  municipioNascimento: string = '';

  racaCor: string = '';
  frequentaEscola: string = '';
  escolaridade: string = '';
  situacaoFamiliar: string = '';
  vinculoEstabelecimento: string = '';
  deficiencia: string = '';

  contatoCelular: string = '';
  contatoResidencial: string = '';
  contatoComercial: string = '';
  contatoEmail: string = '';

  cpf: string = '';

  showModal: boolean = false;
  modalMessage: string = '';
  isModalVisible: boolean = false;

  // ✅ INJEÇÃO DO ROUTER ADICIONADA NO CONSTRUTOR
  constructor(private http: HttpClient, private router: Router) {}

  salvarPaciente() {
    if (!this.nomeCompleto || this.nomeCompleto.trim() === '') {
      alert('Nome Completo é obrigatório!');
      return;
    }

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

    this.http.post('http://localhost:8080/api/pacientes', paciente).subscribe({
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
    // ✅ REDIRECIONAMENTO APÓS FECHAR A MODAL
    this.router.navigate(['/lista']); // Altere para a rota desejada
  }
}
