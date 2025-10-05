import { Routes } from '@angular/router';
import { Logado } from './entrada/logado/logado';
import { Login } from './entrada/login/login';
import { Logado as logadoFuncionario } from './funcionario/logado/logado';
import { Logado as logadoAdmin } from './admin/logado/logado';
import { Logado as logadoCliente } from './cliente/logado/logado';
import { ListaComponent } from './lista/lista.component';

import { AuthGuard } from './auth-guard';
import { CadastroComponent } from './cadastro/cadastro.component';
import { UserCadastroComponent } from './user-cadastro/user-cadastro.component';
import { UploadComponent } from './upload/upload.component';


export const routes: Routes = [

    {
        path: '',
        redirectTo: 'lista',
        pathMatch: 'full'
    },
    {
        path: 'login',
        component: Login
    },
    {
        path: 'user-cadastro',
        component: UserCadastroComponent,
        canActivate: [AuthGuard]

    },
    {   
        path: 'lista',
        component: ListaComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'cadastro',
        component: CadastroComponent,
        canActivate: [AuthGuard]
    },
    { 
        path: 'upload',
        component: UploadComponent 
    },

];
