import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Paciente } from '../model/paciente.model';

@Injectable({
  providedIn: 'root'
})
export class PacienteService {
  constructor(private http: HttpClient) { }

  // Método para listar todos os pacientes
  listar(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>('http://localhost:8091/api/paciente'); 
  }

  // Método para gravar (criar) um novo paciente
  public gravar(obj: Paciente): Observable<Paciente> {
    return this.http.post<Paciente>('http://localhost:8091/api/paciente', obj);
  }

  // Método para buscar um paciente por código (ID)
  buscarPorCodigo(codigo: number): Observable<Paciente> {
    return this.http.get<Paciente>(`http://localhost:8091/api/paciente/${codigo}`);
  }

  // Método para atualizar os dados de um paciente
  updatePaciente(id: number, paciente: Paciente): Observable<Paciente> {
    return this.http.put<Paciente>(`http://localhost:8091/api/paciente/${id}`, paciente);
  }

  // Método para remover um paciente
  remover(codigo: number): Observable<{ mensagem: string }> {
    return this.http.delete<{ mensagem: string }>(`http://localhost:8091/api/paciente/${codigo}`);
  }

  // Método para buscar um paciente por ID (se necessário)
  getPaciente(id: number): Observable<Paciente> {
    return this.http.get<Paciente>(`http://localhost:8091/api/paciente/${id}`);
  }
}
