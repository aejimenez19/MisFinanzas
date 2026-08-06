import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

import { formatCurrency, formatShortDate } from '../../../../shared/format';

@Component({
  selector: 'app-summary-cards',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './summary-cards.html',
  styleUrl: './summary-cards.css'
})
export class SummaryCards {
  readonly availableBalance = input.required<number>();
  readonly monthIncomes = input.required<number>();
  readonly monthExpenses = input.required<number>();
  readonly nextPaymentAmount = input<number>(0);
  readonly nextPaymentDate = input<string | null>(null);
  readonly daysUntilDue = input(0);

  readonly monthNet = computed(() => this.monthIncomes() - this.monthExpenses());

  hasNextPayment(date: string | null): boolean {
    return date !== null && date.trim().length > 0;
  }

  formatCurrency(amount: number): string {
    return formatCurrency(amount);
  }

  formatShortDate(date: string): string {
    return formatShortDate(date);
  }
}