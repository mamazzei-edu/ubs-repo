import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Role } from "../model/role.model";
import { environment } from "../environment";

@Injectable({
    providedIn: 'root',
})
export class RoleService {
    private apiUrl = environment.apiUrl + '/api/roles';

    constructor(private http: HttpClient) { }

    // Listar todos os usuários
    listarRoles(): Observable<any[]> {
        return this.http.get<Role[]>(this.apiUrl);  // Requisição GET para listar roles
    }
}

