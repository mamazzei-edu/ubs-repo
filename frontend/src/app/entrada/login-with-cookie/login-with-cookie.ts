import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormGroup, FormControl, Validators, FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatLabel } from '@angular/material/form-field';
import { MatCardContent } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { LoginClienteCookie } from './login-cliente-cookie';
import { Router } from '@angular/router';


import { LoginServiceCookie } from './login-service-cookie';
import { LoginResponseCookie } from './login-models-cookie';
// import { AuthService } from '../logado/auth-service';

@Component({
  selector: 'app-login-with-cookie',
  imports: [CommonModule, MatCardModule, MatLabel, MatCardContent, MatFormFieldModule, MatInputModule, FormsModule, ReactiveFormsModule, MatButtonModule],
  templateUrl: './login-with-cookie.html',
  styleUrl: './login-with-cookie.scss'
})
export class LoginWithCookie {
  loginResponse!: LoginResponseCookie | undefined;
  loginCliente: LoginClienteCookie = LoginClienteCookie.newLoginClienteCookie();
  loginForm: FormGroup<{ email: FormControl<string | null>; password: FormControl<string | null>; }>;
  constructor(private formBuilder: FormBuilder, private loginService: LoginServiceCookie, private router: Router) {
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
      next: (response: LoginResponseCookie | undefined) => {
        if (response === undefined) {
          console.error('Erro na autenticação: Resposta de login não definida');
          return;
        } else if (!response.token) {
          console.error('Erro na autenticação: Token não encontrado na resposta');
          return;
        } else {
          console.log('Login bem-sucedido:', response);
          this.loginResponse = response;
          // this.authService.saveToken(this.loginResponse);
          // if (this.loginResponse.roles.includes('ROLE_SUPER_ADMIN')) {
          //   this.router.navigate(['/admin']);
          // } else if (this.loginResponse.roles.includes('ROLE_ADMIN')) {
          //   this.router.navigate(['/funcionario']);
          // } else {
          //   this.router.navigate(['/cliente']);
          // }
        }
      },
      error: erro => console.error('Erro no login:', erro)
    });
  }
}
