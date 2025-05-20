import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../service/login.service';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
<form [formGroup]="form" (ngSubmit)="login()">
  <fieldset>
    <legend>Login</legend>
    <div class="form-field">
      <label>Email:</label>
      <input name="email" formControlName="email" type="email" />
      <div *ngIf="form.controls.email.invalid && form.controls.email.touched" style="color:red">
        Email é obrigatório
      </div>
    </div>
    <div class="form-field">
      <label>Password:</label>
      <input name="password" formControlName="password" type="password" />
      <div *ngIf="form.controls.password.invalid && form.controls.password.touched" style="color:red">
        Senha é obrigatória
      </div>
    </div>
  </fieldset>
  <div class="form-buttons">
    <button class="button button-primary" type="submit" [disabled]="form.invalid">Login</button>
  </div>
</form>
  `,
  styles: [`
    /* seus estilos aqui, se quiser */
  `]
})
export class LoginComponent {
  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  login() {
    if (this.form.valid) {
      const val = this.form.value;
      this.authService.login(val.email, val.password).subscribe({
        next: () => {
          console.log('User is logged in');
          this.router.navigateByUrl('/');
        },
        error: (err) => {
          console.error('Login failed', err);
          // aqui você pode mostrar mensagem de erro pro usuário
        }
      });
    }
  }
}
