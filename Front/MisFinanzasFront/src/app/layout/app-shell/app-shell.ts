import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
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

  readonly currentUser = this.authService.currentUser;

  readonly userMenuOpen = signal(false);

  readonly initials = computed(() => {
    const user = this.currentUser();
    if (!user) {
      return null;
    }
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  });

  readonly fullName = computed(() => {
    const user = this.currentUser();
    return user ? `${user.firstName} ${user.lastName}` : '';
  });

  readonly userEmail = computed(() => this.currentUser()?.email ?? '');

  readonly navItems: NavItem[] = [
    { label: 'Panel', icon: 'dashboard', link: '/dashboard', navigate: true },
    { label: 'Transacciones', icon: 'swap_horiz', link: '/transactions', navigate: true },
    { label: 'Tarjetas de crédito', icon: 'credit_card', link: '/credit-cards', navigate: true }
  ];

  readonly mobileNavItems: NavItem[] = [...this.navItems];

  toggleUserMenu(): void {
    this.userMenuOpen.update((open) => !open);
  }

  onLogout(): void {
    this.authService.logout().subscribe();
    this.router.navigate(['/login']);
  }
}