import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Paciente } from '../model/paciente';
 
@Injectable({
  providedIn: 'root'
})
export class PacienteService {
  constructor(private http : HttpClient) { }
  remover(codigo: number) {
    throw new Error('Method not implemented.');
  }
  
  gravar(obj : Paciente) : Observable<Object>{
    return this.http.post<String>("http://localhost:8091/api/paciente",obj);
  }
  alterar(obj : Paciente) : Observable<Object>{
    return this.http.put("http://localhost:8091/api/paciente",obj);
  }
 
 remove(codigo : number) : Observable<Object>{
  return this.http.delete("http://localhost:8091/api/paciente/" + codigo);
}
 
ler(codigo : number) : Observable<Object>{
  return this.http.get("http://localhost:8091/api/paciente/" + codigo);
}
 
listar() : Observable<Object>{
  return this.http.get("http://localhost:8091/api/pacientes");
}
 
}