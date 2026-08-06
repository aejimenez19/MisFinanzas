import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import type { ApiError } from '../../models/api-error';
import { AuthService } from '../../services/auth.service';

type RegisterControl = 'firstName' | 'lastName' | 'email' | 'password';

@Component({
  selector: 'app-register',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  readonly passwordVisible = signal(false);
  readonly loading = signal(false);
  readonly submitError = signal<string | null>(null);

  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  readonly form = new FormGroup({
    firstName: new FormControl('', [Validators.required, Validators.minLength(2)]),
    lastName: new FormControl('', [Validators.required, Validators.minLength(2)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(8)])
  });

  hasError(control: RegisterControl): boolean {
    const c = this.form.controls[control];
    return c.touched && c.invalid;
  }

  errorMessage(control: RegisterControl): string {
    const c = this.form.controls[control];
    if (!c.touched || !c.errors) {
      return '';
    }
    if (c.errors['required']) {
      return this.requiredMessages[control];
    }
    if (c.errors['email']) {
      return 'Ingresa un correo electrónico válido.';
    }
    if (c.errors['minlength']) {
      return this.minLengthMessages[control];
    }
    return 'Campo inválido.';
  }

  togglePasswordVisibility(): void {
    this.passwordVisible.update((visible) => !visible);
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { firstName, lastName, email, password } = this.form.getRawValue();
    this.loading.set(true);
    this.submitError.set(null);

    this.authService
      .register(firstName ?? '', lastName ?? '', email ?? '', password ?? '')
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

  private readonly requiredMessages: Record<RegisterControl, string> = {
    firstName: 'El nombre es obligatorio.',
    lastName: 'El apellido es obligatorio.',
    email: 'El correo electrónico es obligatorio.',
    password: 'La contraseña es obligatoria.'
  };

  private readonly minLengthMessages: Record<RegisterControl, string> = {
    firstName: 'El nombre debe tener al menos 2 caracteres.',
    lastName: 'El apellido debe tener al menos 2 caracteres.',
    email: '',
    password: 'La contraseña debe tener al menos 8 caracteres.'
  };

  private mapError(err: unknown): string {
    if (isApiError(err)) {
      if (err.status === 409) {
        return 'Ya existe una cuenta con este correo electrónico.';
      }
      if (err.status === 400) {
        return err.message || 'Revisa los datos ingresados e inténtalo nuevamente.';
      }
    }
    return 'No se pudo crear la cuenta. Inténtalo nuevamente.';
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
