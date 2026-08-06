import { ChangeDetectionStrategy, Component, computed, input, signal } from '@angular/core';

import { formatCurrency, formatMonthLabel } from '../../../../shared/format';
import type { MonthlyFlow } from '../../models/dashboard';

type Range = '6M' | '1Y' | 'ALL';

interface Bar {
  month: string;
  incomes: number;
  expenses: number;
  incomeHeight: number;
  expenseHeight: number;
}

@Component({
  selector: 'app-cashflow-chart',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './cashflow-chart.html',
  styleUrl: './cashflow-chart.css'
})
export class CashflowChart {
  readonly flow = input<MonthlyFlow[]>([]);

  readonly range = signal<Range>('6M');
  readonly ranges: Range[] = ['6M', '1Y', 'ALL'];

  readonly bars = computed<Bar[]>(() => {
    const visible = this.visibleFlow;
    const max = Math.max(1, ...visible.flatMap((m) => [m.incomes, m.expenses]));
    return visible.map((m) => ({
      month: m.month,
      incomes: m.incomes,
      expenses: m.expenses,
      incomeHeight: (m.incomes / max) * 100,
      expenseHeight: (m.expenses / max) * 100
    }));
  });

  readonly hasData = computed(() => this.bars().some((b) => b.incomes > 0 || b.expenses > 0));

  setRange(range: Range): void {
    this.range.set(range);
  }

  formatMonth(month: string): string {
    return formatMonthLabel(month);
  }

  formatCurrency(amount: number): string {
    return formatCurrency(amount);
  }

  private get visibleFlow(): MonthlyFlow[] {
    const all = [...this.flow()].sort((a, b) => a.month.localeCompare(b.month));
    const range = this.range();
    if (range === 'ALL') {
      return all;
    }
    return all.slice(Math.max(0, all.length - (range === '6M' ? 6 : 12)));
  }
}