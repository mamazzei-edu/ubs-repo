import { Injectable, Inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Paciente } from '../model/paciente.model';

@Injectable({
  providedIn: 'root',
})
export class PacienteService {
  private apiUrl = 'http://localhost:8080/api/pacientes';

  constructor(@Inject(HttpClient) private http: HttpClient) {}

  /** 🔹 Lista pacientes (modo simples, sem DataTables) */
  listarPacientes(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(this.apiUrl);
  }

  /** 🔹 Listagem via DataTables (server-side) */
  listarPacientesAjax(params: any): Observable<any> {
    // O DataTables envia parâmetros como start, length, search[value], etc.
    let httpParams = new HttpParams();
    Object.keys(params).forEach((key) => {
      httpParams = httpParams.append(key, params[key]);
    });

    return this.http.get<any>(`${this.apiUrl}/datatable`, { params: httpParams });
  }

  /** 🔹 Buscar paciente por ID */
  buscarPacientePorId(id: string): Observable<Paciente> {
    return this.http.get<Paciente>(`${this.apiUrl}/${id}`);
  }

  /** 🔹 Excluir paciente */
  excluirPaciente(id: string): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }

  /** 🔹 Editar paciente */
  editarPaciente(id: string, paciente: Paciente): Observable<Paciente> {
    return this.http.put<Paciente>(`${this.apiUrl}/${id}`, paciente);
  }

  /** 🔹 Criar novo paciente */
  gravar(paciente: Paciente): Observable<Paciente> {
    return this.http.post<Paciente>(this.apiUrl, paciente);
  }

  /** 🔹 Upload de ficha em PDF */
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
}
