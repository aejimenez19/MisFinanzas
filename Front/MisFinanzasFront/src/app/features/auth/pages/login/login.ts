import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { ApiError } from '../../models/api-error';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  readonly passwordVisible = signal(false);
  readonly loading = signal(false);
  readonly submitError = signal<string | null>(null);

  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  readonly features = [
    {
      icon: 'shield',
      title: 'Seguridad empresarial',
      description: 'Cifrado de extremo a extremo y autenticación reforzada'
    },
    {
      icon: 'monitoring',
      title: 'Análisis en tiempo real',
      description: 'Dashboards ejecutivos con datos consolidados al instante'
    },
    {
      icon: 'verified_user',
      title: 'Cumplimiento normativo',
      description: 'Auditoría completa y trazabilidad de cada operación'
    }
  ] as const;

  readonly form = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
    remember: new FormControl(false)
  });

  hasError(control: 'email' | 'password'): boolean {
    const c = this.form.controls[control];
    return c.touched && c.invalid;
  }

  togglePasswordVisibility(): void {
    this.passwordVisible.update((visible) => !visible);
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { email, password, remember } = this.form.getRawValue();
    this.loading.set(true);
    this.submitError.set(null);

    this.authService
      .login(email ?? '', password ?? '', remember ?? false)
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.router.navigate(['/dashboard']);
        },
        error: (err: unknown) => {
          this.loading.set(false);
          this.submitError.set(this.mapError(err));
        }
      });
  }

  private mapError(err: unknown): string {
    if (isApiError(err) && err.status === 401) {
      return 'Credenciales inválidas. Verifica tu correo y contraseña.';
    }
    return 'No se pudo conectar con el servidor. Inténtalo nuevamente.';
  }
}

function isApiError(value: unknown): value is ApiError {
  return (
    typeof value === 'object' &&
    value !== null &&
    'message' in value &&
    'status' in value
  );
}