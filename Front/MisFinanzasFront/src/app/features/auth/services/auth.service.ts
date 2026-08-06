import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { catchError, of, tap, throwError } from 'rxjs';

import type { AuthResponse } from '../models/auth-response';
import type { LoginRequest } from '../models/login-request';
import type { RefreshTokenRequest } from '../models/refresh-token-request';
import type { RegisterRequest } from '../models/register-request';
import type { UserResponse } from '../models/user-response';
import {
  clearTokenPair,
  getAccessToken,
  getStoredRefreshToken,
  getStoredRemember,
  saveTokenPair
} from './token-storage';
import { environment } from '../../../../environments/environment';

export const AUTH_API_BASE_URL = `${environment.apiUrl}/api/auth`;

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly user = signal<UserResponse | null>(null);

  readonly currentUser = this.user.asReadonly();

  constructor(private readonly http: HttpClient) {}

  login(email: string, password: string, remember: boolean) {
    const body: LoginRequest = { email, password };
    return this.http.post<AuthResponse>(`${AUTH_API_BASE_URL}/login`, body).pipe(
      tap((response) => this.handleAuthResponse(response, remember))
    );
  }

  register(firstName: string, lastName: string, email: string, password: string) {
    const body: RegisterRequest = { firstName, lastName, email, password };
    return this.http.post<AuthResponse>(`${AUTH_API_BASE_URL}/register`, body).pipe(
      tap((response) => this.handleAuthResponse(response, true))
    );
  }

  refresh() {
    const refreshToken = getStoredRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }
    const body: RefreshTokenRequest = { refreshToken };
    return this.http.post<AuthResponse>(`${AUTH_API_BASE_URL}/refresh`, body).pipe(
      tap((response) => this.handleAuthResponse(response, getStoredRemember()))
    );
  }

  logout() {
    const refreshToken = getStoredRefreshToken();
    this.user.set(null);
    clearTokenPair();
    if (refreshToken) {
      return this.http
        .post<void>(`${AUTH_API_BASE_URL}/logout`, { refreshToken })
        .pipe(catchError(() => of(undefined)));
    }
    return of(undefined);
  }

  getAccessToken(): string | null {
    return getAccessToken();
  }

  isAuthenticated(): boolean {
    return getAccessToken() !== null;
  }

  private handleAuthResponse(response: AuthResponse, remember: boolean): void {
    saveTokenPair({
      accessToken: response.accessToken,
      refreshToken: response.refreshToken,
      remember
    });
    this.user.set(response.user);
  }
}
