import { Component } from '@angular/core';
import { PacienteService } from '../service/paciente.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { RouterModule, Router } from '@angular/router';
import { apiUrl } from '../core/api';

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
  // 🔹 Informações Pessoais
  nomeCompleto: string = '';
  nomeSocial: string = '';
  nomeMae: string = '';
  nomePai: string = '';
  dataNascimento: string = '';
  sexo: string = '';
  cpf: string = '';
  cns: string = '';
  prontuario: string = '';
  nacionalidade: string = '';
  municipioNascimento: string = '';
  racaCor: string = '';
  etnia: string = '';

  // 🔹 Dados Educacionais e Sociais
  frequentaEscola: string = '';
  escolaridade: string = '';
  situacaoFamiliar: string = '';
  ocupacao: string = '';

  // 🔹 Deficiências e OPM
  deficiente: string = '';
  visual: string = '';
  auditiva: string = '';
  motora: string = '';
  intelectual: string = '';
  opm: string = '';

  // 🔹 Contatos
  telefoneCelular: string = '';
  telefoneResidencial: string = '';
  telefoneComercial: string = '';
  email: string = '';
  contato: string = '';

  // 🔹 Endereço
  cep: string = '';
  logradouro: string = '';
  numero: string = '';
  bairro: string = '';
  complemento: string = '';
  uf: string = '';
  municipioResidencia: string = '';
  distritoAdministrativo: string = '';
  tipoLogradouro: string = '';
  origemEndereco: string = '';
  referencia: string = '';

  // 🔹 Documentos
  rg: string = '';
  orgaoEmissor: string = '';
  pisPasepNis: string = '';
  cnh: string = '';
  ctps: string = '';
  tituloEleitor: string = '';
  passaporte: string = '';

  // 🔹 Vínculos e Cadastro
  estabelecimentoVinculo: string = '';
  estabelecimentoCadastro: string = '';

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
      // 🔸 Informações Pessoais
      nomeCompleto: this.nomeCompleto,
      nomeSocial: this.nomeSocial,
      nomeMae: this.nomeMae,
      nomePai: this.nomePai,
      dataNascimento: this.dataNascimento,
      sexo: this.sexo,
      cpf: this.cpf,
      cns: this.cns,
      prontuario: this.prontuario,
      nacionalidade: this.nacionalidade,
      municipioNascimento: this.municipioNascimento,
      racaCor: this.racaCor,
      etnia: this.etnia,

      // 🔸 Dados Educacionais e Sociais
      frequentaEscola: this.frequentaEscola,
      escolaridade: this.escolaridade,
      situacaoFamiliar: this.situacaoFamiliar,
      ocupacao: this.ocupacao,

      // 🔸 Deficiências e OPM
      deficiente: this.deficiente,
      visual: this.visual,
      auditiva: this.auditiva,
      motora: this.motora,
      intelectual: this.intelectual,
      opm: this.opm,

      // 🔸 Contatos
      telefoneCelular: this.telefoneCelular,
      telefoneResidencial: this.telefoneResidencial,
      telefoneComercial: this.telefoneComercial,
      email: this.email,
      contato: this.contato,

      // 🔸 Endereço
      cep: this.cep,
      logradouro: this.logradouro,
      numero: this.numero,
      bairro: this.bairro,
      complemento: this.complemento,
      uf: this.uf,
      municipioResidencia: this.municipioResidencia,
      distritoAdministrativo: this.distritoAdministrativo,
      tipoLogradouro: this.tipoLogradouro,
      origemEndereco: this.origemEndereco,
      referencia: this.referencia,

      // 🔸 Documentos
      rg: this.rg,
      orgaoEmissor: this.orgaoEmissor,
      pisPasepNis: this.pisPasepNis,
      cnh: this.cnh,
      ctps: this.ctps,
      tituloEleitor: this.tituloEleitor,
      passaporte: this.passaporte,

      // 🔸 Vínculos e Cadastro
      estabelecimentoVinculo: this.estabelecimentoVinculo,
      estabelecimentoCadastro: this.estabelecimentoCadastro,
    };

    this.http.post(apiUrl('api/pacientes'), paciente).subscribe({
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
