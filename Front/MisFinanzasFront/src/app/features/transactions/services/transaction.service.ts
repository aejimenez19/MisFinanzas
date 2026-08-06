import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, map, Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import type { CategoryType } from '../models/category';
import type { Transaction } from '../models/transaction';

interface TransactionEntry {
  id: number;
  description: string;
  amount: number;
  categoryId: number;
  categoryName: string;
  movementDate: string;
}

interface TransactionEntryResponse extends TransactionEntry {
  createdAt: string;
  updatedAt: string;
}

export interface TransactionFilters {
  categoryId?: number;
  from?: string;
  to?: string;
}

export interface CreateTransactionPayload {
  description: string;
  amount: number;
  categoryId: number;
  movementDate: string;
}

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private readonly expensesUrl = `${environment.apiUrl}/api/expenses`;
  private readonly incomesUrl = `${environment.apiUrl}/api/incomes`;

  constructor(private readonly http: HttpClient) {}

  list(filters: TransactionFilters = {}): Observable<Transaction[]> {
    const params = this.toParams(filters);
    return forkJoin({
      expenses: this.http.get<TransactionEntry[]>(this.expensesUrl, { params }),
      incomes: this.http.get<TransactionEntry[]>(this.incomesUrl, { params })
    }).pipe(
      map(({ expenses, incomes }) =>
        [...this.toTransactions(expenses, 'EXPENSE'), ...this.toTransactions(incomes, 'INCOME')].sort((a, b) =>
          b.movementDate.localeCompare(a.movementDate)
        )
      )
    );
  }

  create(type: CategoryType, payload: CreateTransactionPayload): Observable<Transaction> {
    const url = type === 'EXPENSE' ? this.expensesUrl : this.incomesUrl;
    return this.http.post<TransactionEntryResponse>(url, payload).pipe(
      map((entry) => ({ ...entry, type }))
    );
  }

  private toTransactions(entries: TransactionEntry[], type: CategoryType): Transaction[] {
    return entries.map((entry) => ({ ...entry, type }));
  }

  private toParams(filters: TransactionFilters): Record<string, string> {
    const params: Record<string, string> = {};
    if (filters.categoryId !== undefined) {
      params['categoryId'] = String(filters.categoryId);
    }
    if (filters.from) {
      params['from'] = filters.from;
    }
    if (filters.to) {
      params['to'] = filters.to;
    }
    return params;
  }
}
