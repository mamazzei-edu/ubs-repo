import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Agendamento,
  AgendamentoRequest,
  StatusAgendamento
} from '../model/agendamento.model';

@Injectable({
  providedIn: 'root'
})
export class AgendamentoService {
  private apiUrl = 'http://localhost:8090/api/agendamentos';

  constructor(private http: HttpClient) {}

  criarAgendamento(req: AgendamentoRequest): Observable<Agendamento> {
    return this.http.post<Agendamento>(this.apiUrl, req);
  }

  listarTodos(): Observable<Agendamento[]> {
    return this.http.get<Agendamento[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Agendamento> {
    return this.http.get<Agendamento>(`${this.apiUrl}/${id}`);
  }

  listarPorPaciente(pacienteId: number): Observable<Agendamento[]> {
    return this.http.get<Agendamento[]>(
      `http://localhost:8090/api/agendamentos/paciente/${pacienteId}`
    );
  }

  listarPorMedico(medicoId: number): Observable<Agendamento[]> {
    return this.http.get<Agendamento[]>(
      `http://localhost:8090/api/agendamentos/medico/${medicoId}`
    );
  }

  verificarDisponibilidade(
    medicoId: number,
    dataHora: string
  ): Observable<{ disponivel: boolean }> {
    return this.http.get<{ disponivel: boolean }>(
      `http://localhost:8090/api/agendamentos/medico/${medicoId}/disponibilidade`,
      { params: { dataHora } }
    );
  }

  cancelarAgendamento(id: number): Observable<Agendamento> {
    return this.http.put<Agendamento>(`${this.apiUrl}/${id}/cancelar`, {});
  }

  confirmarAgendamento(id: number): Observable<Agendamento> {
    return this.http.put<Agendamento>(`${this.apiUrl}/${id}/confirmar`, {});
  }

  marcarComoRealizado(id: number): Observable<Agendamento> {
    return this.http.put<Agendamento>(`${this.apiUrl}/${id}/realizado`, {});
  }

  marcarFalta(id: number): Observable<Agendamento> {
    return this.http.put<Agendamento>(`${this.apiUrl}/${id}/falta`, {});
  }

  reagendar(
    id: number,
    dataHoraConsulta: string
  ): Observable<Agendamento> {
    return this.http.put<Agendamento>(
      `${this.apiUrl}/${id}/reagendar`,
      { dataHoraConsulta }
    );
  }

  atualizarStatus(
    id: number,
    status: StatusAgendamento
  ): Observable<Agendamento> {
    return this.http.put<Agendamento>(
      `${this.apiUrl}/${id}/status`,
      { status }
    );
  }

  atualizarObservacoes(
    id: number,
    observacoes: string
  ): Observable<Agendamento> {
    return this.http.put<Agendamento>(
      `${this.apiUrl}/${id}/observacoes`,
      { observacoes }
    );
  }

  buscarProximosAgendamentos(
    pacienteId: number
  ): Observable<Agendamento[]> {
    return this.http.get<Agendamento[]>(
      `http://localhost:8090/api/agendamentos/paciente/${pacienteId}/proximos`
    );
  }

  buscarProximosAgendamentosMedico(
    medicoId: number
  ): Observable<Agendamento[]> {
    return this.http.get<Agendamento[]>(
      `http://localhost:8090/api/agendamentos/medico/${medicoId}/proximos`
    );
  }

  deletarAgendamento(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}