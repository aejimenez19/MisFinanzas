import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest
} from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, finalize, Observable, shareReplay, switchMap, throwError } from 'rxjs';

import type { AuthResponse } from '../models/auth-response';
import { AUTH_API_BASE_URL, AuthService } from '../services/auth.service';

let refreshInProgress$: Observable<AuthResponse> | null = null;

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  const isAuthRequest = req.url.startsWith(AUTH_API_BASE_URL);
  const accessToken = authService.getAccessToken();

  if (!isAuthRequest && accessToken) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${accessToken}` }
    });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status !== 401 || isAuthRequest) {
        return throwError(() => error);
      }
      const router = inject(Router);
      return refreshAndRetry(req, next, authService, router, error);
    })
  );
};

function refreshAndRetry(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
  authService: AuthService,
  router: Router,
  originalError: HttpErrorResponse
): Observable<HttpEvent<unknown>> {
  if (!refreshInProgress$) {
    refreshInProgress$ = authService.refresh().pipe(
      shareReplay({ bufferSize: 1, refCount: true }),
      finalize(() => {
        refreshInProgress$ = null;
      })
    );
  }

  return refreshInProgress$.pipe(
    switchMap(() => {
      const newAccessToken = authService.getAccessToken();
      if (!newAccessToken) {
        return throwError(() => originalError);
      }
      const retriedReq = req.clone({
        setHeaders: { Authorization: `Bearer ${newAccessToken}` }
      });
      return next(retriedReq);
    }),
    catchError(() => {
      authService.logout().subscribe();
      router.navigate(['/login']);
      return throwError(() => originalError);
    })
  );
}
