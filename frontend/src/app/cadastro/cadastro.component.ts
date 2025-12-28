import { Component } from '@angular/core';
import { PacienteService } from '../service/paciente.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { RouterModule, Router } from '@angular/router';
import { environment } from '../environment';


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
  prontuario: string = '';

  // 🔹 Controle de modal
  showModal: boolean = false;
  modalMessage: string = '';
  isModalVisible: boolean = false;

  // 🔹 Controle de modo edição
  modoEdicao: boolean = false;
  pacienteId: number | null = null;
  showDuplicateModal: boolean = false;

  constructor(private http: HttpClient, private router: Router, private pacienteService: PacienteService) { }

  verificarProntuario() {
    if (this.prontuario && this.prontuario.trim() !== '') {
      this.pacienteService.buscarPacientePorProntuario(this.prontuario).subscribe({
        next: (pacienteExistente) => {
          // Paciente encontrado - abrir modal e carregar dados
          this.showDuplicateModal = true;
          this.carregarDadosPaciente(pacienteExistente);
        },
        error: () => {
          // Prontuário não existe - continuar normalmente
          this.modoEdicao = false;
        }
      });
    }
  }

  carregarDadosPaciente(paciente: any) {
    this.pacienteId = paciente.id;
    this.modoEdicao = true;
    this.nomeCompleto = paciente.nomeCompleto || '';
    this.nomeSocial = paciente.nomeSocial || '';
    this.nomeMae = paciente.nomeMae || '';
    this.nomePai = paciente.nomePai || '';
    this.dataNascimento = paciente.dataNascimento || '';
    this.sexo = paciente.sexo || '';
    this.nacionalidade = paciente.nacionalidade || '';
    this.municipioNascimento = paciente.municipioNascimento || '';
    this.racaCor = paciente.racaCor || '';
    this.frequentaEscola = paciente.frequentaEscola || '';
    this.escolaridade = paciente.escolaridade || '';
    this.situacaoFamiliar = paciente.situacaoFamiliar || '';
    this.vinculoEstabelecimento = paciente.vinculoEstabelecimento || '';
    this.deficiente = paciente.deficiente || '';
    this.contatoCelular = paciente.telefoneCelular || '';
    this.contatoResidencial = paciente.telefoneResidencial || '';
    this.contatoComercial = paciente.telefoneComercial || '';
    this.contatoEmail = paciente.email || '';
    this.cpf = paciente.cpf || '';
    this.rg = paciente.rg || '';
    this.orgaoEmissor = paciente.orgaoEmissor || '';
    this.uf = paciente.uf || '';
    this.pisPasepNis = paciente.pisPasepNis || '';
    this.cnh = paciente.cnh || '';
    this.ctps = paciente.ctps || '';
    this.tituloEleitor = paciente.tituloEleitor || '';
    this.passaporte = paciente.passaporte || '';
    this.cep = paciente.cep || '';
    this.logradouro = paciente.logradouro || '';
    this.numero = paciente.numero || '';
    this.bairro = paciente.bairro || '';
    this.complemento = paciente.complemento || '';
    this.ocupacao = paciente.ocupacao || '';
    this.utilizaAlgumaOPM = paciente.utilizaAlgumaOPM || '';
    this.visual = paciente.visual || '';
    this.auditiva = paciente.auditiva || '';
    this.motora = paciente.motora || '';
    this.intelectual = paciente.intelectual || '';
  }

  closeDuplicateModal() {
    this.showDuplicateModal = false;
  }

  salvarPaciente() {
    // VALIDAÇÃO DE CAMPOS OBRIGATÓRIOS
    if (!this.nomeCompleto || this.nomeCompleto.trim() === '') {
      alert('❌ ERRO: O campo "Nome Completo" é obrigatório!');
      return;
    }

    if (!this.prontuario || this.prontuario.trim() === '') {
      alert('❌ ERRO: O campo "Número de Prontuário" é obrigatório!');
      return;
    }

    if (!this.dataNascimento || this.dataNascimento.trim() === '') {
      alert('❌ ERRO: O campo "Data de Nascimento" é obrigatório!');
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
      prontuario: this.prontuario,
    };

    if (this.modoEdicao && this.pacienteId) {
      // Modo edição - atualizar paciente existente
      this.pacienteService.editarPaciente(this.pacienteId.toString(), paciente).subscribe({
        next: (data) => {
          console.log('✅ Paciente atualizado com sucesso:', data);
          this.openModal();
        },
        error: (err) => {
          console.error('❌ Erro ao atualizar paciente:', err);
          const mensagemErro = err.error?.message || err.error || 'Erro ao atualizar paciente.';
          alert(mensagemErro);
        },
      });
    } else {
      // Modo cadastro - criar novo paciente
      this.http.post(environment.apiUrl + '/api/pacientes', paciente).subscribe({
        next: (data) => {
          console.log('✅ Paciente salvo com sucesso:', data);
          this.openModal();
        },
        error: (err) => {
          console.error('❌ Erro ao salvar paciente:', err);
          const mensagemErro = err.error?.message || err.error || 'Erro ao salvar paciente.';
          alert(mensagemErro);
        },
      });
    }
  }

  openModal() {
    this.isModalVisible = true;
  }

  closeModal() {
    this.isModalVisible = false;
    this.router.navigate(['/lista']); // ajuste a rota se necessário
  }
}
