import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // Importa CommonModule para o *ngIf
import { FormsModule } from '@angular/forms'; // Importa FormsModule para [(ngModel)]

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, FormsModule], // Inclui CommonModule e FormsModule no array imports
  templateUrl: './cadastro.component.html',
  styleUrls: ['./cadastro.component.css'],
})
export class CadastroComponent {
  nomeCompleto = '';
  nomeSocial = '';
  nomeMae = '';
  nomePai = '';
  dataNascimento = '';
  sexo = '';
  nacionalidade = '';
  municipioNascimento = '';
  racaCor = '';
  frequentaEscola = '';
  contatoCelular = '';
  contatoResidencial = '';
  contatoEmail = '';
}
