import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Paciente } from '../model/paciente.model';  // Modelo do paciente, se necessário
import { CommonModule } from '@angular/common'; // ✅ Importando o módulo necessário


@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [CommonModule], // ✅ Adicionando CommonModule para habilitar o pipe 'date'
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css']
})
export class UploadComponent {
  paciente: Paciente = new Paciente(); // 🔹 Garante que nunca seja nulo
  mensagem: string = '';


  // URL da API para o backend onde o arquivo será enviado
  private apiUrl = 'http://localhost:8080/api/upload';
  selectedFile: any;

  constructor(private http: HttpClient) {}

  // Método para manipular o arquivo selecionado
  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  // Método para enviar o arquivo ao backend
  uploadFile(): void {
    if (!this.selectedFile) {
      this.mensagem = 'Por favor, selecione um arquivo antes de enviar.';
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile, this.selectedFile.name);

    // Envia o arquivo para o backend
    this.http.post(this.apiUrl, formData).subscribe({
      next: (response: any) => {
        console.log('Arquivo enviado com sucesso!', response);
        this.mensagem = 'Arquivo enviado com sucesso!';
        this.carregarFicha(response.codigoPaciente); // Carregar a ficha do paciente
      },
      error: (erro) => {
        console.error('Erro ao enviar o arquivo:', erro);
        this.mensagem = 'Erro ao enviar o arquivo.';
      }
    });
  }

  // Método para carregar a ficha do paciente
  carregarFicha(codigoPaciente: number): void {
    this.http.get<Paciente>(`http://localhost:8080/api/paciente/${codigoPaciente}`).subscribe({
      next: (paciente: Paciente) => {
        this.paciente = paciente;
        console.log('Paciente encontrado:', paciente);
      },
      error: (erro) => {
        console.error('Erro ao carregar os dados do paciente:', erro);
        this.mensagem = 'Erro ao carregar os dados do paciente.';
      }
    });
  }
}
