import { Routes } from '@angular/router';
import { CadastroComponent } from './cadastro/cadastro.component';
import { ListaComponent } from './lista/lista.component';
import { UploadComponent } from './upload/upload.component';
import { Component } from '@angular/core';
import { HttpClient,HttpClientModule } from '@angular/common/http';


export const routes: Routes = [
    {path : "cadastro", component : CadastroComponent},
    {path : "lista", component :ListaComponent},
    {path : "upload" , component : UploadComponent},
    {path : '', redirectTo: '/cadastro', pathMatch: 'full' } 
];
