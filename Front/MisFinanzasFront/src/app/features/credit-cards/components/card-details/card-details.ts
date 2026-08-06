import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { formatCurrency, formatPercent, formatShortDate } from '../../../../shared/format';
import type { CreditCard, CreditCardStatement } from '../../models/credit-card';

@Component({
  selector: 'app-card-details',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './card-details.html',
  styleUrl: './card-details.css'
})
export class CardDetails {
  readonly card = input.required<CreditCard>();

  outstandingStatements(): CreditCardStatement[] {
    return (this.card().statements ?? []).filter((statement) => statement.remainingAmount > 0);
  }

  nextPaymentAmount(): number {
    const outstanding = this.outstandingStatements();
    return outstanding.length > 0 ? outstanding[0].remainingAmount : 0;
  }

  formatCurrency(amount: number): string {
    return formatCurrency(amount);
  }

  formatPercent(value: number): string {
    return formatPercent(value);
  }

  formatShortDate(date: string): string {
    return formatShortDate(date);
  }

  usageWidth(): number {
    return Math.min(100, this.card().limitUsagePercent);
  }
}
