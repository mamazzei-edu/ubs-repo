import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';  // Para usar ngModel
import { RouterModule } from '@angular/router';  // Para o roteamento
import { CommonModule } from '@angular/common';  // Para diretivas comuns
import { HttpClientModule } from '@angular/common/http';  // Importa o HttpClientModule

@Component({
  selector: 'app-root',
  standalone: true,  // Componente standalone
  imports: [CommonModule, FormsModule, RouterModule, HttpClientModule],  // Adiciona o HttpClientModule aqui
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'E-SUS';
  nomeCompleto: string = '';
  nomeSocial: string = '';
  nomeMae: string = '';
  nomePai: string = '';
  cpf: string = '';
  dataNascimento: string = '';

  constructor(private http: HttpClient) {}

  // Método para enviar dados com POST
  salvarPaciente() {
    const paciente = {
      nomeCompleto: this.nomeCompleto,
      nomeSocial: this.nomeSocial,
      nomeMae: this.nomeMae,
      nomePai: this.nomePai,
      cpf: this.cpf,
      dataNascimento: this.dataNascimento
    };

    this.http.post('http://localhost:8090/api/pacientes', paciente).subscribe({
      next: (data) => {
        console.log('Paciente salvo com sucesso:', data);
      },
      error: (err) => {
        console.error('Erro ao salvar paciente:', err);
      }
    });
  }
}
