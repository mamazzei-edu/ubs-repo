import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormGroup, FormControl, Validators, FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { LoginCliente } from './login-cliente';
import { Router } from '@angular/router';

import { LoginService } from './login-service';
import { LoginResponse } from './login-models';
import { AuthService } from '../logado/auth-service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, MatCardModule, MatFormFieldModule, MatInputModule, FormsModule, ReactiveFormsModule, MatButtonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss'] // Corrigido
})
export class Login implements OnInit {
  errorMessage: string | null = null;
  loginResponse!: LoginResponse | undefined;
  loginCliente: LoginCliente = LoginCliente.newLoginCliente();
  loginForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private loginService: LoginService,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required] // Senha obrigatória
    });
  }

  ngOnInit(): void {
    // Não precisa recriar o formGroup aqui, apenas vincular valueChanges
    this.loginForm.valueChanges.subscribe(value => {
      this.loginCliente.email = value.email || '';
      this.loginCliente.password = value.password || '';
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Preencha todos os campos obrigatórios corretamente.';
      return;
    }

    this.loginService.logar(this.loginCliente).subscribe({
      next: (response: LoginResponse) => {
        if (!response.roles || response.roles.length === 0) {
          this.errorMessage = 'Erro na autenticação. Verifique usuário e senha.';
          return;
        }

        console.log('Login bem-sucedido:', response);
        this.loginResponse = response;
        this.authService.saveToken(this.loginResponse);

        if (this.loginResponse.roles.includes('ROLE_SUPER_ADMIN')) {
          this.router.navigate(['/user-cadastro']);
        } else if (this.loginResponse.roles.includes('ROLE_ADMIN')) {
          this.router.navigate(['/lista']);
        } else {
          this.router.navigate(['/upload']);
        }
      },
      error: erro => console.error('Erro no login:', erro)
    });
  }
}
