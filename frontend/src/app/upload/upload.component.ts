import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from "@angular/forms";
import { HttpClient } from '@angular/common/http'
import { ReactiveFormsModule, FormsModule } from '@angular/forms'
import { PacienteService } from '../service/paciente.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.css',
  providers: [PacienteService]

})

export class UploadComponent implements OnInit {
  public form!: FormGroup;
  file: File | null = null;
  pacienteSelecionado: any = null;
  mostrarModalEditar: boolean = false;

  constructor(public fb: FormBuilder, private http: HttpClient, private pacienteService: PacienteService) { }
  ngOnInit() {
    this.form = this.fb.group({
      ficha: [null],
    })
  }

  uploadFile(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.file = file;
    }
  }

  submitForm() {
    if (this.file) {
      const formData = new FormData();
      formData.append('ficha', this.file, this.file.name);
      this.http.post('http://localhost:8090/arquivos', formData)
        .subscribe(
          {
            next: (dados) => {
              this.mostrarModalEditar = true;
              this.pacienteSelecionado = dados;
            },
            error: () => {
            },
          })

    }

  }
  
  atualizarPaciente(): void {
    console.log('Paciente a ser atualizado:', this.pacienteSelecionado);  // Verifique se o objeto está correto
    this.pacienteService.editarPaciente(this.pacienteSelecionado.id, this.pacienteSelecionado).subscribe({
      next: (paciente) => {
        console.log('Paciente atualizado com sucesso:', paciente);
      },
      error: (err) => {
        console.error('Erro ao atualizar paciente:', err);
      }
    });
  }


}
