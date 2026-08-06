import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';

import type { Category } from '../../models/category';
import type { Transaction } from '../../models/transaction';
import { CategoryService } from '../../services/category.service';
import { TransactionService } from '../../services/transaction.service';
import { TransactionForm } from './components/transaction-form';
import { TransactionList } from './components/transaction-list';

@Component({
  selector: 'app-transactions',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [TransactionForm, TransactionList],
  templateUrl: './transactions.html',
  styleUrl: './transactions.css'
})
export class Transactions {
  readonly transactions = signal<Transaction[]>([]);
  readonly expenseCategories = signal<Category[]>([]);
  readonly incomeCategories = signal<Category[]>([]);
  readonly loading = signal(true);
  readonly loadError = signal<string | null>(null);

  readonly allCategories = computed(() => [...this.expenseCategories(), ...this.incomeCategories()]);

  private readonly transactionService = inject(TransactionService);
  private readonly categoryService = inject(CategoryService);

  constructor() {
    this.loadCategories();
    this.loadTransactions();
  }

  onTransactionCreated(transaction: Transaction): void {
    this.transactions.update((current) =>
      [...current.filter((item) => item.id !== transaction.id), transaction].sort((a, b) =>
        b.movementDate.localeCompare(a.movementDate)
      )
    );
  }

  private loadCategories(): void {
    forkJoin({
      expenses: this.categoryService.list('EXPENSE'),
      incomes: this.categoryService.list('INCOME')
    }).subscribe({
      next: ({ expenses, incomes }) => {
        this.expenseCategories.set(expenses);
        this.incomeCategories.set(incomes);
      }
    });
  }

  private loadTransactions(): void {
    this.loading.set(true);
    this.loadError.set(null);
    this.transactionService.list().subscribe({
      next: (transactions) => {
        this.transactions.set(transactions);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.loadError.set('No se pudieron cargar las transacciones.');
      }
    });
  }
}
