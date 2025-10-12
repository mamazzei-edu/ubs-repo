import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Medico } from '../model/medico.model';

@Injectable({
  providedIn: 'root'
})
export class MedicoService {
  private apiUrl = 'http://localhost:8090/api/medicos';

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<Medico[]> {
    return this.http.get<Medico[]>(this.apiUrl);
  }

  listarAtivos(): Observable<Medico[]> {
    return this.http.get<Medico[]>(`${this.apiUrl}/ativos`);
  }

  buscarPorId(id: number): Observable<Medico> {
    return this.http.get<Medico>(`${this.apiUrl}/${id}`);
  }

  buscarPorCrm(crm: string): Observable<Medico> {
    return this.http.get<Medico>(`${this.apiUrl}/crm/${crm}`);
  }

  buscarPorEspecialidade(especialidade: string): Observable<Medico[]> {
    return this.http.get<Medico[]>(`${this.apiUrl}/especialidade/${especialidade}`);
  }

  buscarPorNome(nome: string): Observable<Medico[]> {
    return this.http.get<Medico[]>(`${this.apiUrl}/nome/${nome}`);
  }

  criarMedico(medico: Partial<Medico>): Observable<Medico> {
    return this.http.post<Medico>(this.apiUrl, medico);
  }

  atualizarMedico(id: number, medico: Partial<Medico>): Observable<Medico> {
    return this.http.put<Medico>(`${this.apiUrl}/${id}`, medico);
  }

  alterarStatus(id: number, ativo: boolean): Observable<Medico> {
    return this.http.put<Medico>(`${this.apiUrl}/${id}/status`, { ativo });
  }

  desativarMedico(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}