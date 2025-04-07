import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css']
})
export class UploadComponent {
uploadFile() {
throw new Error('Method not implemented.');
}
onFileSelected($event: Event) {
throw new Error('Method not implemented.');
}
  selectedFile: File | null = null;
  extractedData: any;
  pacienteService: any;

  constructor(private http: HttpClient) {}

  uploadFicha(codigo: number, event: any) {
    const file: File = event.target.files[0]; // Obtém o arquivo do input
  
    if (file) {
      this.pacienteService.uploadFicha(codigo, file).subscribe((response: { mensagem: any; }) => {
        console.log(response.mensagem);
        alert('Ficha enviada com sucesso!');
      }, (error: any) => {
        alert('Erro ao enviar a ficha.');
      });
    }
  }
  
  }

