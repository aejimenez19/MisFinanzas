import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';

import { formatDate } from '../../../../shared/format';
import type { DashboardSummary } from '../../models/dashboard';
import { DashboardService } from '../../services/dashboard.service';
import { CashflowChart } from '../../components/cashflow-chart/cashflow-chart';
import { CreditCards } from '../../components/credit-cards/credit-cards';
import { RecentTransactions } from '../../components/recent-transactions/recent-transactions';
import { SummaryCards } from '../../components/summary-cards/summary-cards';

@Component({
  selector: 'app-dashboard',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
  imports: [CashflowChart, CreditCards, RecentTransactions, SummaryCards]
})
export class Dashboard {
  private readonly dashboardService = inject(DashboardService);

  readonly summary = signal<DashboardSummary | null>(null);
  readonly loading = signal(true);

  constructor() {
    this.load();
  }

  reload(): void {
    this.load();
  }

  formatDate(date: string): string {
    return formatDate(date);
  }

  private load(): void {
    this.loading.set(true);
    this.dashboardService
      .summary()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (summary) => this.summary.set(summary),
        error: () => this.summary.set(null)
      });
  }
}