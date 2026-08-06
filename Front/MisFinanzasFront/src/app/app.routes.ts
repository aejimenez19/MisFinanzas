import { Routes } from '@angular/router';

import { authGuard } from './features/auth/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/pages/login/login').then((m) => m.Login)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/pages/register/register').then((m) => m.Register)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./layout/app-shell/app-shell').then((m) => m.AppShell),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/pages/dashboard/dashboard').then((m) => m.Dashboard)
      },
      {
        path: 'transactions',
        loadComponent: () =>
          import('./features/transactions/pages/transactions/transactions').then((m) => m.Transactions)
      },
      {
        path: 'credit-cards',
        loadComponent: () =>
          import('./features/credit-cards/pages/credit-cards/credit-cards').then((m) => m.CreditCards)
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: 'login' }
];