import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { User } from "../model/user.model";
import { Role } from "../model/role.model";

@Injectable({
    providedIn: 'root',
})
export class RoleService {
    private apiUrl = 'http://localhost:8090/api/roles';  // URL do backend diretamente no código

    constructor(private http: HttpClient) { }

    // Listar todos os usuários
    listarRoles(): Observable<any[]> {
        return this.http.get<Role[]>(this.apiUrl);  // Requisição GET para listar roles
    }
}

