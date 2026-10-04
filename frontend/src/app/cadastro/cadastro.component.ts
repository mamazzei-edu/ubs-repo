import { Component } from '@angular/core';
import { PacienteService } from '../service/paciente.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { RouterModule, Router } from '@angular/router';
import { apiUrl, mensagemDoErro } from '../core/api';
import { cpfValido, mascararCpf, normalizarCpf } from '../core/cpf';

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

  // 🔹 Validação de CPF e erros do servidor
  cpfInvalido: boolean = false;
  pacienteComMesmoCpf: { id: number; nomeCompleto: string } | null = null;
  erroMensagem: string = '';
  salvando: boolean = false;

  constructor(private http: HttpClient, private router: Router) {}

  aoDigitarCpf(valor: string) {
    this.cpf = mascararCpf(valor);
    this.cpfInvalido = false;
    this.pacienteComMesmoCpf = null;
  }

  /**
   * Ao sair do campo: valida os dígitos e pergunta ao backend se o CPF já
   * existe — o aviso aparece antes de o usuário preencher o resto da ficha.
   */
  verificarCpf() {
    if (!normalizarCpf(this.cpf)) {
      return;
    }
    this.cpfInvalido = !cpfValido(this.cpf);
    if (this.cpfInvalido) {
      return;
    }
    this.http.get<any>(apiUrl(`api/pacientes/cpf/${normalizarCpf(this.cpf)}`)).subscribe({
      next: (paciente) => (this.pacienteComMesmoCpf = paciente),
      // 404 = CPF livre. Outros erros não bloqueiam: o POST valida de novo.
      error: () => (this.pacienteComMesmoCpf = null),
    });
  }

  abrirCadastroExistente() {
    if (this.pacienteComMesmoCpf) {
      this.router.navigate(['/lista'], { queryParams: { id: this.pacienteComMesmoCpf.id } });
    }
  }

  salvarPaciente() {
    this.erroMensagem = '';
    if (!this.nomeCompleto || this.nomeCompleto.trim() === '') {
      alert('Nome Completo é obrigatório!');
      return;
    }
    if (!cpfValido(this.cpf)) {
      this.cpfInvalido = true;
      this.erroMensagem = 'Informe um CPF válido.';
      return;
    }
    if (this.pacienteComMesmoCpf) {
      this.erroMensagem = 'Este CPF já está cadastrado. Abra o cadastro existente para alterá-lo.';
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

    this.salvando = true;
    this.http.post(apiUrl('api/pacientes'), paciente).subscribe({
      next: (data) => {
        this.salvando = false;
        console.log('✅ Paciente salvo com sucesso:', data);
        this.openModal();
      },
      error: (err) => {
        this.salvando = false;
        console.error('❌ Erro ao salvar paciente:', err);
        // 409: outro paciente já tem o CPF (alguém cadastrou entre a
        // verificação e o envio). O corpo traz o id dele.
        if (err.status === 409 && err.error?.pacienteExistenteId) {
          this.pacienteComMesmoCpf = { id: err.error.pacienteExistenteId, nomeCompleto: 'outro paciente' };
        }
        this.erroMensagem = mensagemDoErro(err, 'Não foi possível salvar o paciente. Tente novamente.');
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
