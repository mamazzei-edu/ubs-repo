import { Component } from '@angular/core';
import { PacienteService } from '../service/paciente.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './cadastro.component.html',
  styleUrls: ['./cadastro.component.css'],
  providers: [PacienteService],
})
export class CadastroComponent {
  // 🔹 Campos principais
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
  deficiente: string = '';

  // 🔹 Contatos
  contatoCelular: string = '';
  contatoResidencial: string = '';
  contatoComercial: string = '';
  contatoEmail: string = '';

  // 🔹 Documentos
  cpf: string = '';
  rg: string = '';
  orgaoEmissor: string = '';
  uf: string = '';
  pisPasepNis: string = '';
  cnh: string = '';
  ctps: string = '';
  tituloEleitor: string = '';
  passaporte: string = '';

  // 🔹 Endereço
  cep: string = '';
  logradouro: string = '';
  numero: string = '';
  bairro: string = '';
  complemento: string = '';

  // 🔹 Outros dados
  ocupacao: string = '';
  utilizaAlgumaOPM: string = '';
  visual: string = '';
  auditiva: string = '';
  motora: string = '';
  intelectual: string = '';

  // 🔹 Controle de modal
  showModal: boolean = false;
  modalMessage: string = '';
  isModalVisible: boolean = false;

  constructor(private http: HttpClient, private router: Router) {}

  salvarPaciente() {
    if (!this.nomeCompleto || this.nomeCompleto.trim() === '') {
      alert('Nome Completo é obrigatório!');
      return;
    }

    const paciente = {
      // 🔸 Dados principais
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
      deficiente: this.deficiente,

      // 🔸 Contatos
      contatoCelular: this.contatoCelular,
      contatoResidencial: this.contatoResidencial,
      contatoComercial: this.contatoComercial,
      contatoEmail: this.contatoEmail,

      // 🔸 Documentos
      cpf: this.cpf,
      rg: this.rg,
      orgaoEmissor: this.orgaoEmissor,
      uf: this.uf,
      pisPasepNis: this.pisPasepNis,
      cnh: this.cnh,
      ctps: this.ctps,
      tituloEleitor: this.tituloEleitor,
      passaporte: this.passaporte,

      // 🔸 Endereço
      cep: this.cep,
      logradouro: this.logradouro,
      numero: this.numero,
      bairro: this.bairro,
      complemento: this.complemento,

      // 🔸 Outros dados
      ocupacao: this.ocupacao,
      utilizaAlgumaOPM: this.utilizaAlgumaOPM,
      visual: this.visual,
      auditiva: this.auditiva,
      motora: this.motora,
      intelectual: this.intelectual,
    };

    this.http.post('http://localhost:8090/api/pacientes', paciente).subscribe({
      next: (data) => {
        console.log('✅ Paciente salvo com sucesso:', data);
        this.openModal();
      },
      error: (err) => {
        console.error('❌ Erro ao salvar paciente:', err);
      },
    });
  }

  openModal() {
    this.isModalVisible = true;
  }

  closeModal() {
    this.isModalVisible = false;
    this.router.navigate(['/lista']); // ajuste a rota se necessário
  }
}
