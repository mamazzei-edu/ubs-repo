import { Routes } from '@angular/router';
import { CadastroComponent } from './cadastro/cadastro.component';
import { ListaComponent } from './lista/lista.component';
import { UploadComponent } from './upload/upload.component';
import { UserCadastroComponent } from './user-cadastro/user-cadastro.component';
import path from 'path';
import { Component } from '@angular/core';
export const routes: Routes = [
    {path : "cadastro", component : CadastroComponent},
    {path : "lista", component :ListaComponent},
    {path : "upload" , component : UploadComponent},
    {path : "user-cadastro", component :UserCadastroComponent}
];
