import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { categoryIcon, formatAmount, formatShortDate } from '../../../../shared/format';
import type { RecentMovement } from '../../models/dashboard';

@Component({
  selector: 'app-recent-transactions',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './recent-transactions.html',
  styleUrl: './recent-transactions.css'
})
export class RecentTransactions {
  readonly movements = input<RecentMovement[]>([]);

  categoryIcon(categoryName: string): string {
    return categoryIcon(categoryName);
  }

  formatAmount(amount: number, type: RecentMovement['type']): string {
    return formatAmount(amount, type);
  }

  formatShortDate(date: string): string {
    return formatShortDate(date);
  }
}