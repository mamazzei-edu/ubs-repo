import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from "@angular/forms";
import { HttpClient } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { PacienteService } from '../service/paciente.service';
import { CommonModule } from '@angular/common';
import { Paciente } from '../model/paciente.model';
import { environment } from './../environment';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css'],
  providers: [PacienteService]
})
export class UploadComponent implements OnInit {
  public form!: FormGroup;
  file: File | null = null;
  pacienteSelecionado: any = null;
  mostrarModalEditar: boolean = false;
  mensagem: string = '';
  nomeInvalido: boolean = false;
  cpfInvalido: boolean = false;
  telefoneInvalido: boolean = false;
  modoEdicao: boolean = false;
  pacienteId: number | null = null;
  showDuplicateModal: boolean = false;

  constructor(public fb: FormBuilder, private http: HttpClient, private pacienteService: PacienteService) { }

  ngOnInit() {
    this.form = this.fb.group({
      ficha: [null],
    });
  }

  // Evita o comportamento padrão (abrir arquivo no navegador)
  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
  }

  // Captura o arquivo ao soltar (drop)
  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();

    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      this.file = event.dataTransfer.files[0];
      event.dataTransfer.clearData();
    }
  }

  // Captura arquivo ao selecionar pelo input
  uploadFile(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.file = file;
    }
  }

  submitForm() {
    if (this.file) {
      console.log('📤 Iniciando upload do arquivo...');
      const formData = new FormData();
      formData.append('ficha', this.file, this.file.name);
      this.http.post(environment.apiUrl + '/arquivos', formData)
        .subscribe({
          next: (dados) => {
            console.log('📄 Dados extraídos do PDF:', dados);
            this.pacienteSelecionado = dados;

            // LIMPAR ESPAÇOS EXTRAS de todos os campos de texto
            if (this.pacienteSelecionado.nomeCompleto) {
              this.pacienteSelecionado.nomeCompleto = this.pacienteSelecionado.nomeCompleto.trim();
            }
            if (this.pacienteSelecionado.prontuario) {
              this.pacienteSelecionado.prontuario = this.pacienteSelecionado.prontuario.trim();
            }

            const data = this.pacienteSelecionado.dataNascimento;
            const [dia, mes, ano] = data.split('/');
            const dataNascimento = `${ano}-${mes}-${dia}`;
            this.pacienteSelecionado.dataNascimento = dataNascimento;
            this.mensagem = '';

            console.log('🔍 INICIANDO VERIFICAÇÃO DE DUPLICAÇÃO...');
            console.log('Nome (após trim):', this.pacienteSelecionado.nomeCompleto);
            console.log('Prontuário (após trim):', this.pacienteSelecionado.prontuario);

            // NÃO MOSTRAR O FORMULÁRIO AINDA - Primeiro verificar duplicação
            this.mostrarModalEditar = false;

            // Verificar se já existe paciente com mesmo nome ou prontuário
            this.verificarDuplicacao();
          },
          error: (erro) => {
            if (erro.error && erro.error.message) {
              this.mensagem = erro.error.message;
            } else if (erro.error && typeof erro.error === 'string' && erro.error.includes('já existe')) {
              this.mensagem = "Este arquivo já foi enviado anteriormente. Não é permitido fazer upload do mesmo arquivo duas vezes.";
            } else {
              this.mensagem = "Erro no upload do arquivo.";
            }
          },
        });
    }
  }

  verificarDuplicacao() {
    console.log('🔍 verificarDuplicacao() chamado');

    // Verificar por prontuário primeiro
    if (this.pacienteSelecionado.prontuario && this.pacienteSelecionado.prontuario.trim() !== '') {
      console.log('🔍 Verificando prontuário:', this.pacienteSelecionado.prontuario);
      this.pacienteService.buscarPacientePorProntuario(this.pacienteSelecionado.prontuario).subscribe({
        next: (pacienteExistente) => {
          // Paciente encontrado por prontuário
          console.log('🚨🚨🚨 DUPLICAÇÃO DETECTADA POR PRONTUÁRIO! 🚨🚨🚨');
          console.log('Paciente existente:', pacienteExistente);
          this.pacienteId = pacienteExistente.id;
          this.modoEdicao = true;
          this.showDuplicateModal = true;
          this.mostrarModalEditar = true;
          console.log('✅ Modal de duplicação ativado!');
          console.log('✅ Modo Edição: true');
          console.log('✅ Botão deve mostrar: ALTERAR');
        },
        error: (erro) => {
          // Prontuário não existe, verificar por nome
          console.log('❌ Prontuário não encontrado (erro ' + erro.status + ')');
          console.log('🔍 Verificando por nome...');
          this.verificarPorNome();
        }
      });
    } else {
      // Se não tem prontuário, verificar por nome
      console.log('⚠️ Sem prontuário, verificando por nome...');
      this.verificarPorNome();
    }
  }

  verificarPorNome() {
    if (this.pacienteSelecionado.nomeCompleto && this.pacienteSelecionado.nomeCompleto.trim() !== '') {
      const nomeParaBuscar = this.pacienteSelecionado.nomeCompleto.trim();
      console.log('🔍 Buscando paciente por nome:', nomeParaBuscar);

      this.http.get(`${environment.apiUrl}/api/pacientes/nome/${encodeURIComponent(nomeParaBuscar)}`).subscribe({
        next: (pacienteExistente: any) => {
          // Paciente encontrado por nome
          console.log('🚨🚨🚨 DUPLICAÇÃO DETECTADA POR NOME! 🚨🚨🚨');
          console.log('Paciente existente:', pacienteExistente);
          this.pacienteId = pacienteExistente.id;
          this.modoEdicao = true;
          this.showDuplicateModal = true;
          this.mostrarModalEditar = true;
          console.log('✅ Modal de duplicação ativado!');
          console.log('✅ Modo Edição: true');
          console.log('✅ Botão deve mostrar: ALTERAR');
        },
        error: (erro) => {
          // Não existe duplicação - modo cadastro
          console.log('✅ Nenhuma duplicação encontrada (erro ' + erro.status + ')');
          console.log('✅ Modo CADASTRO ativado');
          this.modoEdicao = false;
          this.showDuplicateModal = false;
          this.mostrarModalEditar = true;
          console.log('✅ Botão deve mostrar: SALVAR');
        }
      });
    } else {
      // Não tem nome, continuar em modo cadastro
      console.log('⚠️ Sem nome completo, modo CADASTRO por padrão.');
      this.modoEdicao = false;
      this.showDuplicateModal = false;
      this.mostrarModalEditar = true;
    }
  }

  closeDuplicateModal() {
    this.showDuplicateModal = false;
  }

  public validarNome() {
    const regex = /^[A-Za-zÀ-ÖØ-öø-ÿ ]+$/;
    this.nomeInvalido = !regex.test(this.pacienteSelecionado.nomeCompleto || '');
  }

  public formatarCPF() {
    let cpf = this.pacienteSelecionado.cpf?.replace(/\D/g, '') || '';
    if (cpf.length > 3) cpf = cpf.replace(/^(\d{3})(\d)/, '$1.$2');
    if (cpf.length > 6) cpf = cpf.replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3');
    if (cpf.length > 9) cpf = cpf.replace(/^(\d{3})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3-$4');
    this.pacienteSelecionado.cpf = cpf;

    this.cpfInvalido = cpf.length !== 14;
  }

  public formatarTelefone() {
    let telefone = this.pacienteSelecionado.telefoneCelular?.replace(/\D/g, '') || '';
    if (telefone.length > 2) telefone = telefone.replace(/^(\d{2})(\d)/, '($1) $2');
    if (telefone.length > 7) telefone = telefone.replace(/(\d{5})(\d)/, '$1-$2');
    this.pacienteSelecionado.telefoneCelular = telefone;

    this.telefoneInvalido = telefone.length !== 15;
  }

  uploadFicha(codigo: number, event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.pacienteService.uploadFicha(codigo, file).subscribe((response: { mensagem: any; }) => {
        alert('Ficha enviada com sucesso!');
      }, (error: any) => {
        alert('Erro ao enviar a ficha.');
      });
    }
  }

  public gravar(pacienteSelecionado: Paciente) {
    console.log('🔵 Tentando gravar paciente. Modo Edição:', this.modoEdicao, 'ID:', this.pacienteId);

    // VALIDAÇÃO DE CAMPOS OBRIGATÓRIOS
    if (!this.pacienteSelecionado.nomeCompleto || this.pacienteSelecionado.nomeCompleto.trim() === '') {
      alert('❌ ERRO: O campo "Nome Completo" é obrigatório!');
      return;
    }

    if (!this.pacienteSelecionado.prontuario || this.pacienteSelecionado.prontuario.trim() === '') {
      alert('❌ ERRO: O campo "Número de Prontuário" é obrigatório!');
      return;
    }

    if (!this.pacienteSelecionado.dataNascimento || this.pacienteSelecionado.dataNascimento.trim() === '') {
      alert('❌ ERRO: O campo "Data de Nascimento" é obrigatório!');
      return;
    }

    if (this.modoEdicao && this.pacienteId) {
      // Modo edição - atualizar paciente existente
      console.log('✏️ Atualizando paciente existente ID:', this.pacienteId);
      this.pacienteService.editarPaciente(this.pacienteId.toString(), this.pacienteSelecionado).subscribe({
        next: (data) => {
          console.log('✅ Paciente atualizado com sucesso!');
          this.mensagem = "Paciente atualizado com sucesso!";
          this.limpar();
        },
        error: (err) => {
          console.error('❌ Erro ao atualizar:', err);
          const mensagemErro = err.error?.message || err.error || 'Erro ao atualizar paciente.';
          this.mensagem = mensagemErro;
          alert(mensagemErro);
        }
      });
    } else {
      // Modo cadastro - criar novo paciente
      console.log('➕ Criando novo paciente');
      this.pacienteService.gravar(this.pacienteSelecionado).subscribe({
        next: (data) => {
          console.log('✅ Paciente registrado com sucesso!');
          this.mensagem = "Paciente registrado com sucesso!";
          this.limpar();
        },
        error: (err) => {
          console.error('❌ Erro ao salvar:', err);
          const mensagemErro = err.error?.message || err.error || 'Erro ao salvar paciente.';
          this.mensagem = mensagemErro;
          alert(mensagemErro);
        }
      });
    }
  }

  public limpar() {
    this.pacienteSelecionado = new Paciente();
    this.nomeInvalido = false;
    this.cpfInvalido = false;
    this.telefoneInvalido = false;
    this.modoEdicao = false;
    this.pacienteId = null;
    this.showDuplicateModal = false;
    this.mostrarModalEditar = false;
    this.file = null;
  }
}
