import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { formatCurrency, formatPercent, formatShortDate } from '../../../../shared/format';
import type { CardBalanceView } from '../../models/dashboard';

@Component({
  selector: 'app-credit-cards',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './credit-cards.html',
  styleUrl: './credit-cards.css'
})
export class CreditCards {
  readonly cards = input<CardBalanceView[]>([]);

  formatCurrency(amount: number): string {
    return formatCurrency(amount);
  }

  formatPercent(value: number): string {
    return formatPercent(value);
  }

  formatShortDate(date: string): string {
    return formatShortDate(date);
  }

  usageWidth(card: CardBalanceView): number {
    return Math.min(100, card.limitUsagePercent);
  }
}