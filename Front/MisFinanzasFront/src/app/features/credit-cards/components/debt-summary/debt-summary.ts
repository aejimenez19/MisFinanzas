import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { formatCurrency } from '../../../../shared/format';
import type { CreditCard } from '../../models/credit-card';

@Component({
  selector: 'app-debt-summary',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './debt-summary.html',
  styleUrl: './debt-summary.css'
})
export class DebtSummary {
  readonly cards = input<CreditCard[]>([]);

  formatCurrency(amount: number): string {
    return formatCurrency(amount);
  }

  totalDebt(): number {
    return this.cards().reduce((sum, card) => sum + card.pendingBalance, 0);
  }
}