import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from "@angular/forms";
import { HttpClient } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { PacienteService } from '../service/paciente.service';
import { CommonModule } from '@angular/common';
import { Paciente } from '../model/paciente.model';

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
      const formData = new FormData();
      formData.append('ficha', this.file, this.file.name);
      this.http.post('http://localhost:8090/arquivos', formData)
        .subscribe({
          next: (dados) => {
            this.mostrarModalEditar = true;
            this.pacienteSelecionado = dados;
            const data = this.pacienteSelecionado.dataNascimento;
            const [dia, mes, ano] = data.split('/');
            const dataNascimento = `${ano}-${mes}-${dia}`;
            this.pacienteSelecionado.dataNascimento = dataNascimento;
          },
          error: () => {
            this.mensagem = "Erro no upload do arquivo.";
          },
        });
    }
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
    this.pacienteService.gravar(this.pacienteSelecionado).subscribe({
      next: (data) => {
        this.mensagem = "Paciente registrado com sucesso!";
        this.limpar();
      },
      error: (msg) => {
        this.mensagem = "Ocorreu um erro, tente mais tarde.";
      }
    });
  }

  public limpar() {
    this.pacienteSelecionado = new Paciente();
    this.nomeInvalido = false;
    this.cpfInvalido = false;
    this.telefoneInvalido = false;
  }
}
