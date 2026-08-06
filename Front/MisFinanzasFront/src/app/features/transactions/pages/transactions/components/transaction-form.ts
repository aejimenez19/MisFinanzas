import { ChangeDetectionStrategy, Component, computed, inject, input, output, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import type { Category, CategoryType } from '../../../models/category';
import type { Transaction } from '../../../models/transaction';
import { TransactionService } from '../../../services/transaction.service';
import { CurrencyInputDirective } from '../../../../../shared/currency-input.directive';

type FormControlName = 'amount' | 'categoryId' | 'movementDate' | 'description';

@Component({
  selector: 'app-transaction-form',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, CurrencyInputDirective],
  templateUrl: './transaction-form.html',
  styleUrl: './transaction-form.css'
})
export class TransactionForm {
  readonly expenseCategories = input.required<Category[]>();
  readonly incomeCategories = input.required<Category[]>();

  readonly created = output<Transaction>();

  readonly type = signal<CategoryType>('EXPENSE');
  readonly submitting = signal(false);

  readonly activeCategories = computed(() =>
    this.type() === 'EXPENSE' ? this.expenseCategories() : this.incomeCategories()
  );

  private readonly transactionService = inject(TransactionService);

  readonly form = new FormGroup({
    amount: new FormControl<number | null>(null, [Validators.required, Validators.min(0.01)]),
    categoryId: new FormControl<number | null>(null, [Validators.required]),
    movementDate: new FormControl<string>(this.today(), [Validators.required]),
    description: new FormControl('', [Validators.required])
  });

  setType(type: CategoryType): void {
    this.type.set(type);
    this.form.controls.categoryId.reset();
  }

  hasError(control: FormControlName): boolean {
    const c = this.form.controls[control];
    return c.touched && c.invalid;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { amount, categoryId, movementDate, description } = this.form.getRawValue();
    this.submitting.set(true);

    this.transactionService
      .create(this.type(), {
        description: description ?? '',
        amount: amount ?? 0,
        categoryId: categoryId ?? 0,
        movementDate: movementDate ?? ''
      })
      .subscribe({
        next: (transaction) => {
          this.submitting.set(false);
          this.form.reset({
            amount: null,
            categoryId: null,
            movementDate: this.today(),
            description: ''
          });
          this.created.emit(transaction);
        },
        error: () => {
          this.submitting.set(false);
        }
      });
  }

  private today(): string {
    return new Date().toISOString().slice(0, 10);
  }
}
