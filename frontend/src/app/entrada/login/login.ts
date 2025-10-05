import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormGroup, FormControl, Validators, FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatLabel } from '@angular/material/form-field';
import { MatCardContent } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { LoginCliente } from './login-cliente';
import { Router } from '@angular/router';


import { LoginService } from './login-service';
import { LoginResponse } from './login-models';
import { AuthService } from '../logado/auth-service';
import { NullLiteral } from 'typescript';

@Component({
  selector: 'app-login',
  imports: [CommonModule, MatCardModule, MatLabel, MatCardContent, MatFormFieldModule, MatInputModule, FormsModule, ReactiveFormsModule, MatButtonModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})

export class Login implements OnInit {
  errorMessage: string | null = null;
  loginResponse!: LoginResponse | undefined;
  loginCliente: LoginCliente = LoginCliente.newLoginCliente();
  loginForm: FormGroup<{ email: FormControl<string | null>; password: FormControl<string | null>; }>;
  constructor(private formBuilder: FormBuilder, private loginService: LoginService, private authService: AuthService, private router: Router) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

  }
  ngOnInit(): void {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required)
    });
    this.loginForm.valueChanges.subscribe(value => {
      this.loginCliente.email = value.email || '';
      this.loginCliente.password = value.password || '';
    });
  }
  onSubmit() {
    this.loginService.logar(this.loginCliente).subscribe({
      next: (response: LoginResponse) => {
        if (response.token == null || response.token === undefined) {
          this.errorMessage = 'Erro na autenticação não foi possível efetuar o login. Verifique seu usuário e senha.';
          return;
        } else {
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
        }
      },
      error: erro => console.error('Erro no login:', erro)
    });
  }



}
