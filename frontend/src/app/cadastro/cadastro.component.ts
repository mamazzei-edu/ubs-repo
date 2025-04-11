import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PacienteService } from '../service/paciente.service';
import { Paciente } from '../model/paciente.model';

@Component({
  selector: 'app-cadastro',
  standalone: true, 
  templateUrl: './cadastro.component.html',
  styleUrls: ['./cadastro.component.css'],
  providers: [PacienteService],
  imports: [CommonModule, FormsModule]
})

export class CadastroComponent {
  public paciente = new Paciente();
  mensagem: string = '';
  nomeInvalido: boolean = false;
  cpfInvalido: boolean = false;
  telefoneInvalido: boolean = false;

  constructor(private service: PacienteService) {}

  // Para validar se o nome contém apenas letras e espaços
  public validarNome() {
    const regex = /^[A-Za-zÀ-ÖØ-öø-ÿ ]+$/;
    this.nomeInvalido = !regex.test(this.paciente.nomeCompleto || '');
  }

  // Para formatar CPF e verifica se segue o padrão 000.000.000-00
  public formatarCPF() {
    let cpf = this.paciente.cpf?.replace(/\D/g, '') || '';
    if (cpf.length > 3) cpf = cpf.replace(/^(\d{3})(\d)/, '$1.$2');
    if (cpf.length > 6) cpf = cpf.replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3');
    if (cpf.length > 9) cpf = cpf.replace(/^(\d{3})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3-$4');
    this.paciente.cpf = cpf;

    this.cpfInvalido = cpf.length !== 14;
  }

  //  Formatar telefone e verifica se segue o padrão (00) 00000-0000
  public formatarTelefone() {
    let telefone = this.paciente.telefoneCelular?.replace(/\D/g, '') || '';
    if (telefone.length > 2) telefone = telefone.replace(/^(\d{2})(\d)/, '($1) $2');
    if (telefone.length > 7) telefone = telefone.replace(/(\d{5})(\d)/, '$1-$2');
    this.paciente.telefoneCelular = telefone;

    this.telefoneInvalido = telefone.length !== 15;
  }

  //  Verificar se todos os campos são válidos antes de enviar
  public gravar() {
    if (this.nomeInvalido) {
      this.mensagem = 'Nome inválido! Apenas letras são permitidas.';
      return;
    }
    if (this.cpfInvalido) {
      this.mensagem = 'CPF inválido! Use o formato 000.000.000-00.';
      return;
    }
    if (this.telefoneInvalido) {
      this.mensagem = 'Telefone inválido! Use o formato (00) 00000-0000.';
      return;
    }

    console.log('Enviando paciente:', this.paciente);

    this.service.gravar(this.paciente).subscribe({
      next: (data) => {
        console.log('Resposta do servidor:', data);
        this.mensagem = "Paciente registrado com sucesso!";
        this.limpar();
      },
      error: (msg) => {
        console.error('Erro ao registrar paciente:', msg);
        this.mensagem = "Ocorreu um erro, tente mais tarde.";
      }
    });
  }

  // Reseta os campos do formulário
  public limpar() {
    this.paciente = new Paciente();
    this.nomeInvalido = false;
    this.cpfInvalido = false;
    this.telefoneInvalido = false;
  }
}
