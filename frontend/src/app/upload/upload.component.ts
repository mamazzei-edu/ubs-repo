import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from "@angular/forms";
import { HttpClient } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { PacienteService } from '../service/paciente.service';
import { CommonModule } from '@angular/common';
import { Paciente } from '../model/paciente.model';
import { apiUrl, mensagemDoErro } from '../core/api';
import { cpfValido, mascararCpf } from '../core/cpf';

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
  // Resultado da leitura da ficha: quando o CPF ja consta na base, o backend
  // devolve o registro gravado com os campos da ficha por cima e a lista do
  // que esta mudando.
  pacienteExistente: boolean = false;
  alteracoes: { campo: string; rotulo: string; valorAtual: string | null; valorNovo: string }[] = [];
  mostrarModalEditar: boolean = false;
  mensagem: string = '';
  nomeInvalido: boolean = false;
  cpfInvalido: boolean = false;
  telefoneInvalido: boolean = false;
  erroGravacao: string = '';

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
    if (!this.file) {
      return;
    }
    const formData = new FormData();
    formData.append('ficha', this.file, this.file.name);

    this.http.post<any>(apiUrl('arquivos'), formData).subscribe({
      next: (resultado) => {
        this.pacienteSelecionado = resultado.paciente;
        this.pacienteExistente = resultado.pacienteExistente;
        this.alteracoes = resultado.alteracoes || [];
        this.pacienteSelecionado.dataNascimento = this.paraDataDoFormulario(
          this.pacienteSelecionado.dataNascimento
        );
        // O backend devolve o CPF so com digitos; na tela, com mascara.
        this.formatarCPF();
        this.mostrarModalEditar = true;
        this.mensagem = this.pacienteExistente
          ? `Paciente ja cadastrado (CPF ${this.pacienteSelecionado.cpf}). ` +
            `${this.alteracoes.length} campo(s) serao alterados ao salvar.`
          : 'Paciente novo. Confira os dados antes de salvar.';
      },
      error: () => {
        this.mensagem = 'Erro no upload do arquivo.';
      },
    });
  }

  // A ficha traz dd/MM/yyyy e o <input type="date"> exige yyyy-MM-dd.
  // O registro vindo do banco ja esta em ISO, e o campo pode vir vazio:
  // o split sem protecao quebrava o fluxo inteiro nesses dois casos.
  private paraDataDoFormulario(valor: string | null | undefined): string {
    if (!valor) {
      return '';
    }
    const partes = valor.split('/');
    if (partes.length === 3) {
      const [dia, mes, ano] = partes;
      return `${ano}-${mes.padStart(2, '0')}-${dia.padStart(2, '0')}`;
    }
    return valor;
  }

  public validarNome() {
    const regex = /^[A-Za-zÀ-ÖØ-öø-ÿ ]+$/;
    this.nomeInvalido = !regex.test(this.pacienteSelecionado.nomeCompleto || '');
  }

  public formatarCPF() {
    const cpf = mascararCpf(this.pacienteSelecionado.cpf);
    this.pacienteSelecionado.cpf = cpf;
    // Completo (14 caracteres) e com digito verificador errado: avisa ja.
    this.cpfInvalido = cpf.length === 14 && !cpfValido(cpf);
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
    this.erroGravacao = '';
    if (!cpfValido(this.pacienteSelecionado.cpf)) {
      this.cpfInvalido = true;
      this.erroGravacao = 'Informe um CPF válido antes de salvar.';
      return;
    }
    const atualizando = this.pacienteExistente;
    this.pacienteService.gravar(this.pacienteSelecionado).subscribe({
      next: () => {
        this.mensagem = atualizando
          ? 'Cadastro do paciente atualizado com sucesso!'
          : 'Paciente registrado com sucesso!';
        this.limpar();
      },
      error: (err) => {
        // 409 = CPF de outro paciente; 400 = CPF invalido ou obrigatorio.
        this.erroGravacao = mensagemDoErro(err, 'Ocorreu um erro, tente mais tarde.');
      },
    });
  }

  public limpar() {
    this.pacienteSelecionado = new Paciente();
    this.pacienteExistente = false;
    this.alteracoes = [];
    this.mostrarModalEditar = false;
    this.nomeInvalido = false;
    this.cpfInvalido = false;
    this.telefoneInvalido = false;
    this.erroGravacao = '';
  }
}
