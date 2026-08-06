import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { categoryIcon, formatCurrency, formatShortDate } from '../../../../shared/format';
import type { CreditCardActivityItem } from '../../models/credit-card';

@Component({
  selector: 'app-card-activity',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './card-activity.html',
  styleUrl: './card-activity.css'
})
export class CardActivity {
  readonly items = input<CreditCardActivityItem[]>([]);
  readonly loading = input(false);
  readonly cutoffDay = input(1);

  formatCurrency(amount: number): string {
    return formatCurrency(amount);
  }

  formatShortDate(date: string): string {
    return formatShortDate(date);
  }

  icon(item: CreditCardActivityItem): string {
    return item.type === 'PAYMENT' ? 'payments' : categoryIcon(item.description);
  }

  isPayment(item: CreditCardActivityItem): boolean {
    return item.type === 'PAYMENT';
  }

  billingLabel(item: CreditCardActivityItem): string | null {
    if (item.type !== 'PURCHASE' || !item.billingCycle) {
      return null;
    }
    return item.billingCycle < this.currentCutoff() ? 'Facturado' : 'Próximo extracto';
  }

  private currentCutoff(): string {
    const cutoffDay = Math.max(1, Math.min(31, this.cutoffDay()));
    const today = new Date();
    const candidate = new Date(today.getFullYear(), today.getMonth(), Math.min(cutoffDay, this.daysInMonth(today)));
    if (candidate.getTime() < today.setHours(0, 0, 0, 0)) {
      return this.iso(new Date(today.getFullYear(), today.getMonth() + 1, Math.min(cutoffDay, this.daysInMonth(today))));
    }
    return this.iso(candidate);
  }

  private daysInMonth(date: Date): number {
    return new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
  }

  private iso(date: Date): string {
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${date.getFullYear()}-${mm}-${dd}`;
  }
}
