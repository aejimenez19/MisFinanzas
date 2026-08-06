import { ChangeDetectionStrategy, Component, computed, input, signal } from '@angular/core';

import { categoryIcon, formatAmount, formatDate, formatMonthLabel } from '../../../../../shared/format';
import type { Category, CategoryType } from '../../../models/category';
import type { Transaction } from '../../../models/transaction';

type Period = 'last30' | 'month' | 'year';

@Component({
  selector: 'app-transaction-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './transaction-list.html',
  styleUrl: './transaction-list.css'
})
export class TransactionList {
  readonly transactions = input<Transaction[]>([]);
  readonly categories = input<Category[]>([]);
  readonly loading = input(false);

  readonly categoryId = signal<number | null>(null);
  readonly period = signal<Period>('month');
  readonly visibleCount = signal(5);

  readonly filtered = computed(() => this.applyFilters(this.transactions()));
  readonly expensesTotal = computed(() => this.sumByType(this.filtered(), 'EXPENSE'));
  readonly incomesTotal = computed(() => this.sumByType(this.filtered(), 'INCOME'));
  readonly balance = computed(() => this.incomesTotal() - this.expensesTotal());
  readonly visibleTransactions = computed(() => this.filtered().slice(0, this.visibleCount()));
  readonly hasMore = computed(() => this.visibleCount() < this.filtered().length);

  readonly monthLabel = computed(() => formatMonthLabel(this.currentMonth()));

  onCategoryChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.categoryId.set(value ? Number(value) : null);
    this.visibleCount.set(5);
  }

  onPeriodChange(event: Event): void {
    this.period.set((event.target as HTMLSelectElement).value as Period);
    this.visibleCount.set(5);
  }

  loadMore(): void {
    this.visibleCount.update((count) => count + 5);
  }

  categoryIcon(categoryName: string): string {
    return categoryIcon(categoryName);
  }

  formatAmount(amount: number, type: CategoryType): string {
    return formatAmount(amount, type);
  }

  formatDate(date: string): string {
    return formatDate(date);
  }

  private sumByType(list: Transaction[], type: CategoryType): number {
    return list.filter((transaction) => transaction.type === type).reduce((sum, transaction) => sum + transaction.amount, 0);
  }

  private applyFilters(list: Transaction[]): Transaction[] {
    const { from, to } = this.dateRange();
    const categoryId = this.categoryId();
    return list.filter((transaction) => {
      if (categoryId !== null && transaction.categoryId !== categoryId) {
        return false;
      }
      return transaction.movementDate >= from && transaction.movementDate <= to;
    });
  }

  private dateRange(): { from: string; to: string } {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const to = this.toISODate(today);

    let from: Date;
    switch (this.period()) {
      case 'last30':
        from = new Date(today);
        from.setDate(from.getDate() - 30);
        break;
      case 'year':
        from = new Date(today.getFullYear(), 0, 1);
        break;
      case 'month':
      default:
        from = new Date(today.getFullYear(), today.getMonth(), 1);
        break;
    }

    return { from: this.toISODate(from), to };
  }

  private toISODate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private currentMonth(): string {
    const today = new Date();
    return `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`;
  }
}
