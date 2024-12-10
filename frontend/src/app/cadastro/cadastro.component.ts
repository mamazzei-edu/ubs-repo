import { PacienteService } from "../service/paciente.service";
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms'; 
import { Paciente } from '../model/paciente';
import { Component } from "@angular/core";


@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cadastro.component.html',
  styleUrl: './cadastro.component.css',
  providers : [PacienteService]
})

export class CadastroComponent {
  public paciente = new Paciente();
 mensagem: string = '';


  constructor(private service: PacienteService) {}


  public gravar() {
    this.service.gravar(this.paciente).subscribe({
      next: (data) => {
        this.mensagem = "Paciente registrado com sucesso!";
        this.limpar();
      },
      error: (msg) => {
        this.mensagem = "Ocorreu um erro, tente mais tarde.";
      }
    });
   
  }
   public limpar() {
    this.paciente = new Paciente;
  } 

    
  }

