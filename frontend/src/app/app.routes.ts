import { Routes } from '@angular/router';
import { Logado } from './entrada/logado/logado';
import { Login } from './entrada/login/login';
import { Logado as logadoFuncionario } from './funcionario/logado/logado';
import { Logado as logadoAdmin } from './admin/logado/logado';
import { Logado as logadoCliente } from './cliente/logado/logado';
import { AuthGuard } from './auth-guard';
import { CadastroComponent } from './cadastro/cadastro.component';
import { ListaComponent } from './cadastroUsuario/lista/lista';
// import { CadastroUsuario } from './cadastroUsuario/cadastroUsuario';
import { UploadComponent } from './upload/upload.component';
//import { ListaUsuario as ListaUsuario } from './listaUsuario/lista_usuario';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


export const routes: Routes = [

    {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    },
    {
        path: 'login',
        component: Login
    },
    {
        path: 'admin',
        component: logadoAdmin,
        canActivate: [AuthGuard]

    },
    {   
        path: 'funcionario',
        component: logadoFuncionario,
        canActivate: [AuthGuard]
    },
    {
        path: 'cliente',
        component: logadoCliente,
        canActivate: [AuthGuard]
    },

    { path: 'cadastro', 
      component: CadastroComponent 
    },
    {
        path: 'cadastroUsuario',
        loadComponent: () => import('./cadastroUsuario/cadastroUsuario')
        .then(m => m.CadastroUsuario)
    },
    { path: 'lista', 
        component: ListaComponent
    },
    { 
        path: 'upload',
        component: UploadComponent 
    },
    // { 
    //     path: 'login', 
    //     loadComponent: () => import('./login/login.component')
    //     .then(m => m.LoginComponent) 
    // },
    { 
        path: '', 
        redirectTo: '/login', 
        pathMatch: 'full' 
    }
];
