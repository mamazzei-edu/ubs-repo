import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Agendamento, AgendamentoRequest, StatusAgendamento } from '../model/agendamento.model';

@Injectable({
  providedIn: 'root'
})
export class AgendamentoService {
  private apiUrl = 'http://localhost:8090/api/agendamentos';

  constructor(private http: HttpClient) { }

  // Criar novo agendamento
  criarAgendamento(agendamento: AgendamentoRequest): Observable<Agendamento> {
    return this.http.post<Agendamento>(this.apiUrl, agendamento);
  }

  // Listar todos os agendamentos
  listarTodos(): Observable<Agendamento[]> {
    return this.http.get<Agendamento[]>(this.apiUrl);
  }

  // Buscar agendamento por ID
  buscarPorId(id: number): Observable<Agendamento> {
    return this.http.get<Agendamento>(`${this.apiUrl}/${id}`);
  }

  // Listar agendamentos por paciente
  listarPorPaciente(pacienteId: number): Observable<Agendamento[]> {
    return this.http.get<Agendamento[]>(`${this.apiUrl}/paciente/${pacienteId}`);
  }

  // Listar agendamentos por médico
  listarPorMedico(medicoId: number): Observable<Agendamento[]> {
    return this.http.get<Agendamento[]>(`${this.apiUrl}/medico/${medicoId}`);
  }

  // Atualizar status do agendamento
  atualizarStatus(id: number, status: StatusAgendamento): Observable<Agendamento> {
    return this.http.put<Agendamento>(`${this.apiUrl}/${id}/status`, { status });
  }

  // Cancelar agendamento
  cancelarAgendamento(id: number): Observable<Agendamento> {
    return this.http.put<Agendamento>(`${this.apiUrl}/${id}/cancelar`, {});
  }

  // Confirmar agendamento
  confirmarAgendamento(id: number): Observable<Agendamento> {
    return this.http.put<Agendamento>(`${this.apiUrl}/${id}/confirmar`, {});
  }

  // Atualizar observações
  atualizarObservacoes(id: number, observacoes: string): Observable<Agendamento> {
    return this.http.put<Agendamento>(`${this.apiUrl}/${id}/observacoes`, { observacoes });
  }

  // Buscar próximos agendamentos do paciente
  buscarProximosAgendamentos(pacienteId: number): Observable<Agendamento[]> {
    return this.http.get<Agendamento[]>(`${this.apiUrl}/paciente/${pacienteId}/proximos`);
  }

  // Deletar agendamento
  deletarAgendamento(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}