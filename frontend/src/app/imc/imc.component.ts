import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

export interface ResultadoImc {
  valor: number;
  classificacao: string;
  classe: 'baixo' | 'adequado' | 'atencao' | 'alto';
}

@Component({
  selector: 'app-imc',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule
  ],
  templateUrl: './imc.component.html',
  styleUrl: './imc.component.scss'
})
export class ImcComponent {
  resultado: ResultadoImc | null = null;

  readonly imcForm;

  constructor(private readonly fb: FormBuilder) {
    this.imcForm = this.fb.nonNullable.group({
      peso: [0, [Validators.required, Validators.min(1), Validators.max(500)]],
      altura: [0, [Validators.required, Validators.min(0.5), Validators.max(2.5)]]
    });
  }

  calcular(): void {
    if (this.imcForm.invalid) {
      this.imcForm.markAllAsTouched();
      this.resultado = null;
      return;
    }

    const { peso, altura } = this.imcForm.getRawValue();
    this.resultado = this.calcularImc(peso, altura);
  }

  limpar(): void {
    this.imcForm.reset({ peso: 0, altura: 0 });
    this.resultado = null;
  }

  calcularImc(peso: number, altura: number): ResultadoImc {
    const valor = Math.round((peso / (altura * altura)) * 10) / 10;

    if (valor < 18.5) {
      return { valor, classificacao: 'Abaixo do peso', classe: 'baixo' };
    }

    if (valor < 25) {
      return { valor, classificacao: 'Peso adequado', classe: 'adequado' };
    }

    if (valor < 30) {
      return { valor, classificacao: 'Sobrepeso', classe: 'atencao' };
    }

    return { valor, classificacao: 'Obesidade', classe: 'alto' };
  }
}
