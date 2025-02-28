import { Routes } from '@angular/router';
import { CadastroComponent } from './cadastro/cadastro.component';
import { ListaComponent } from './lista/lista.component';
import { UploadComponent } from './upload/upload.component';
import { LoginComponent } from './login/login.component';

// ✅ Adicione "export" antes da constante "routes"
export const routes: Routes = [
    { path: "cadastro", component: CadastroComponent },
    { path: "login", component: LoginComponent },
    { path: "lista", component: ListaComponent },
    { path: "upload", component: UploadComponent },
    { path: "", redirectTo: "lista", pathMatch: "full" }, // ✅ Redireciona para Lista ao acessar "/"
    { path: "**", redirectTo: "login" } // ✅ Redireciona para Login se a rota não existir
];
