import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Role } from '../model/role.model';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:8090/api/usuarios';  // URL do backend diretamente no código

  constructor(private http: HttpClient) {}

  // Listar todos os usuários
  listarUsuarios(): Observable<any[]> {
    return this.http.get<User[]>(this.apiUrl);  // Requisição GET para listar usuários
  }

  // Buscar usuário por ID
  buscarUsuarioPorId(id: string): Observable<any> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);  // Requisição GET passando o ID do usuário
  }

  // Cadastrar novo usuário
  criarUsuario(usuario: any): Observable<any> {
    return this.http.post<User>(this.apiUrl, usuario);  // Requisição POST para cadastrar um novo usuário
  }

  // Atualizar usuário
  editarUsuario(id: string, usuario: any): Observable<any> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, usuario);  // Requisição PUT passando o ID e os dados do usuário
  }

  // Excluir usuário
  excluirUsuario(id: string): Observable<any> {
    return this.http.delete<User>(`${this.apiUrl}/${id}`);  // Requisição DELETE passando o ID do usuário
  }
}
