import { Injectable, Inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Paciente } from '../model/paciente.model';
import { environment } from './../environment';

@Injectable({
  providedIn: 'root',
})

export class PacienteService {
  private apiUrl = environment.apiUrl + '/api/pacientes';  // URL do backend diretamente no código

  constructor(@Inject(HttpClient) private http: HttpClient) { }

  listarPacientes(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);  // Requisição GET para listar pacientes
  }

  listarPacientesPaginados(page: number, size: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}?page=${page}&size=${size}`);
  }

  buscarPacientePorId(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);  // Requisição GET passando o ID do paciente
  }

  buscarPacientePorProntuario(prontuario: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/prontuario/${prontuario}`);  // Requisição GET passando o prontuário do paciente
  }

  excluirPaciente(id: string): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);  // Requisição DELETE passando o ID do paciente
  }

  editarPaciente(id: string, paciente: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, paciente);  // Requisição PUT passando o id e os dados do paciente
  }

  //  Upload de ficha em PDF
  uploadFicha(id: number, file: File): Observable<{ mensagem: string }> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    const headers = new HttpHeaders();

    return this.http.post<{ mensagem: string }>(
      `${this.apiUrl}/${id}/upload-ficha`,
      formData,
      { headers }
    );
  }

  //  Criar um novo paciente
  gravar(paciente: Paciente): Observable<Paciente> {
    return this.http.post<Paciente>(this.apiUrl, paciente);
  }

  buscarPorNomeParcial(nome: string): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(`${this.apiUrl}/buscar/nome-parcial/${nome}`);
  }

  buscarPorCpfParcial(cpf: string): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(`${this.apiUrl}/buscar/cpf-parcial/${cpf}`);
  }
}
