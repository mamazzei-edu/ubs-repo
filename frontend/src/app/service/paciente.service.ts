import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PacienteService {
  private apiUrl = 'http://localhost:8090/api/pacientes';  // URL do backend diretamente no código

  constructor(private http: HttpClient) {}

  listarPacientes(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);  // Requisição GET para listar pacientes
  }

  buscarPacientePorId(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);  // Requisição GET passando o ID do paciente
  }

  excluirPaciente(id: string): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);  // Requisição DELETE passando o ID do paciente
  }

  editarPaciente(id: string, paciente: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, paciente);  // Requisição PUT passando o id e os dados do paciente
  }
  
  
  
}
