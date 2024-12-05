import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Paciente } from '../model/paciente';
import { PacienteService } from '../service/paciente.service';  // Certifique-se de que o serviço está correto

@Component({
  selector: 'app-lista',
  standalone : true,
  imports : [CommonModule],
  templateUrl: './lista.component.html',
  styleUrls: ['./lista.component.css'],
  providers : [PacienteService]
})
export class ListaComponent {
mensagem : String = " ";
pacientes : Paciente []= [];


  constructor(private service: PacienteService) {
    this.listar();
  }

listar(){
  this.service.listar().subscribe({
    next:(data) => {this.pacientes = data; },
    error : (msg) => {this.mensagem = "ocorreu um erro"}
  });
}


  }

  
