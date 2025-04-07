import { Routes } from '@angular/router';
import { CadastroComponent } from './cadastro/cadastro.component';
import { ListaComponent } from './lista/lista.component';
import { UploadComponent } from './upload/upload.component';
import { Component } from '@angular/core';
import { LoginComponent } from './login/login.component';



export const routes: Routes = [
    
    {path : "cadastro", component : CadastroComponent},
    {path : "login", component : LoginComponent},
    {path : "lista", component :ListaComponent},
    {path : "upload" , component : UploadComponent},
    {path : " " , component : ListaComponent } 
    
];