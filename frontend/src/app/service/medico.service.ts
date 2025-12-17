import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root'
})
export class MedicoService {
  private apiUrl = 'http://localhost:8080/api/medicos';

  constructor(private http: HttpClient) { }

  listarTodos(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  listarAtivos(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/ativos`);
  }

  buscarPorId(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  buscarPorCrm(crm: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/crm/${crm}`);
  }

  buscarPorEspecialidade(especialidade: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/especialidade/${especialidade}`);
  }

  buscarPorNome(nome: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/nome/${nome}`);
  }

  criarMedico(medico: Partial<User>): Observable<User> {
    return this.http.post<User>(this.apiUrl, medico);
  }

  atualizarMedico(id: number, medico: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, medico);
  }

  alterarStatus(id: number, ativo: boolean): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}/status`, { ativo });
  }

  desativarMedico(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}