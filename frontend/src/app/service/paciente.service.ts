import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Paciente } from '../model/paciente.model';

@Injectable({
  providedIn: 'root'
})
export class PacienteService {
  updatePaciente(codigo: number, paciente: Paciente): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/pacientes/${codigo}`, paciente);
}

  private apiUrl = 'http://localhost:8091/api/paciente'; // Definir uma variável base para a URL

  constructor(private http: HttpClient) {}

  // Listar todos os pacientes
  listar(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(this.apiUrl);
  }

  //  Criar um novo paciente
  gravar(paciente: Paciente): Observable<Paciente> {
    return this.http.post<Paciente>(this.apiUrl, paciente);
  }

  //  Buscar um paciente por código (ID)
  buscarPorCodigo(codigo: number): Observable<Paciente> {
    return this.http.get<Paciente>(`${this.apiUrl}/${codigo}`);
  }

  //  Atualizar os dados de um paciente
  atualizar(codigo: number, paciente: Paciente): Observable<{ mensagem: string }> {
    return this.http.put<{ mensagem: string }>(`${this.apiUrl}/${codigo}`, paciente);
  }

  

  //  Remover um paciente
  remover(codigo: number): Observable<{ mensagem: string }> {
    return this.http.delete<{ mensagem: string }>(`${this.apiUrl}/${codigo}`);
  }

  //  Upload de ficha em PDF
  uploadFicha(codigo: number, file: File): Observable<{ mensagem: string }> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    const headers = new HttpHeaders(); 

    return this.http.post<{ mensagem: string }>(
      `${this.apiUrl}/${codigo}/upload-ficha`,
      formData,
      { headers }
    );
  }
}
