import { Routes } from '@angular/router';
import { ListaComponent } from './lista/lista.component';
import { UploadComponent } from './upload/upload.component';
import { CadastroUsuarioComponent } from './cadastroUsuario/cadastroUsuario.component';
import { CadastroComponent } from './cadastro/cadastro.component';

export const routes: Routes = [
  { path: 'cadastro', component: CadastroComponent }, 
  {path: 'cadastroUsuario' ,  component: CadastroUsuarioComponent},
  { path: 'lista', component: ListaComponent },
  { path: 'upload', component: UploadComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];
