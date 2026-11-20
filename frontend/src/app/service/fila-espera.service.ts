
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PacienteFila {
  id: number;
  nomeCompleto: string;
}

@Injectable({
  providedIn: 'root'
})
export class FilaEsperaApiService {

  private apiUrl = 'http://localhost:8080/api/fila-espera';

  constructor(private http: HttpClient) {}

  listarFila(): Observable<PacienteFila[]> {
    return this.http.get<PacienteFila[]>(this.apiUrl);
  }

  adicionarPaciente(id: number): Observable<PacienteFila> {
    return this.http.post<PacienteFila>(`${this.apiUrl}/${id}`, {});
  }

  chamarProximo(): Observable<PacienteFila | null> {
    return this.http.post<PacienteFila | null>(
      `${this.apiUrl}/proximo`,
      {}
    );
  }

  removerPaciente(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
