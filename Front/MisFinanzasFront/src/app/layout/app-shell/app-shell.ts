import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthService } from '../../features/auth/services/auth.service';

interface NavItem {
  label: string;
  icon: string;
  link: string;
  navigate?: boolean;
}

@Component({
  selector: 'app-shell',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app-shell.html',
  styleUrl: './app-shell.css'
})
export class AppShell {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly navItems: NavItem[] = [
    { label: 'Panel', icon: 'dashboard', link: '/dashboard', navigate: true },
    { label: 'Transacciones', icon: 'swap_horiz', link: '/transactions', navigate: true },
    { label: 'Tarjetas de crédito', icon: 'credit_card', link: '/credit-cards', navigate: true }
  ];

  readonly placeholderItems: NavItem[] = [
    { label: 'Historial', icon: 'history', link: '/history', navigate: false }
  ];

  readonly mobileNavItems: NavItem[] = [...this.navItems, ...this.placeholderItems];

  onLogout(): void {
    this.authService.logout().subscribe();
    this.router.navigate(['/login']);
  }
}