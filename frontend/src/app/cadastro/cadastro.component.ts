import { PacienteService } from "../service/paciente.service";
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // Para usar ngIf, ngFor
import { FormsModule } from '@angular/forms'; // Para usar ngModel
import { Paciente } from '../model/paciente';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cadastro.component.html',
  styleUrl: './cadastro.component.css'
})

export class CadastroComponent {
  public paciente = new Paciente();
  // Dados do paciente


  mensagem: string = '';

  // Injeção do serviço
  constructor(private service: PacienteService) {}

  // Método para gravar o paciente
  public gravar() {
    this.service.gravar(this.paciente).subscribe({
      next: (data) => {
        this.mensagem = "Paciente registrado com sucesso!";
      },
      error: (msg) => {
        this.mensagem = "Ocorreu um erro, tente mais tarde.";
      }
    });
    //this.limpar();
  }


  // Método para limpar o formulário
  public limpar() {
    this.paciente = {
      codigo : 0,
      nomeCompleto: '',
      nomeSocial: '',
      nomeMae: '',
      nomePai: '',
      dataNascimento: new Date,
      sexo: 'Masculino',
      nacionalidade: '',
      municipioNascimento: '',
      racaCor: 'Parda',
      frequentaEscola: 'Sim',
      contatoCelular: '',
      contatoResidencial: '',
      contatoEmail: ''
    };
  }
}
