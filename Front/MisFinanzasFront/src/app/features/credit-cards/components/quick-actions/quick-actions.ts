import { ChangeDetectionStrategy, Component, input, inject, output, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { formatCurrency } from '../../../../shared/format';
import { CurrencyInputDirective } from '../../../../shared/currency-input.directive';
import type { CreditCard } from '../../models/credit-card';
import { CreditCardsService } from '../../services/credit-cards.service';

type ActionType = 'PURCHASE' | 'PAYMENT';

@Component({
  selector: 'app-quick-actions',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, CurrencyInputDirective],
  templateUrl: './quick-actions.html',
  styleUrl: './quick-actions.css'
})
export class QuickActions {
  readonly card = input.required<CreditCard>();

  readonly created = output<void>();

  private readonly creditCardsService = inject(CreditCardsService);

  readonly action = signal<ActionType>('PURCHASE');
  readonly submitting = signal(false);
  readonly error = signal<string | null>(null);

  readonly purchaseForm = new FormGroup({
    amount: new FormControl<number | null>(null, [Validators.required, Validators.min(0.01)]),
    description: new FormControl('', [Validators.required]),
    purchaseDate: new FormControl<string>(this.today(), [Validators.required])
  });

  readonly paymentForm = new FormGroup({
    amount: new FormControl<number | null>(null, [Validators.required, Validators.min(0.01)]),
    paymentDate: new FormControl<string>(this.today(), [Validators.required])
  });

  setAction(action: ActionType): void {
    this.action.set(action);
    this.error.set(null);
  }

  fillCurrentBalance(): void {
    this.paymentForm.controls.amount.setValue(this.card().pendingBalance);
  }

  formatCurrency(amount: number): string {
    return formatCurrency(amount);
  }

  hasPurchaseError(field: 'amount' | 'description' | 'purchaseDate'): boolean {
    const control = this.purchaseForm.controls[field];
    return control.touched && control.invalid;
  }

  hasPaymentError(field: 'amount' | 'paymentDate'): boolean {
    const control = this.paymentForm.controls[field];
    return control.touched && control.invalid;
  }

  submitPurchase(): void {
    if (this.purchaseForm.invalid) {
      this.purchaseForm.markAllAsTouched();
      return;
    }
    const { amount, description, purchaseDate } = this.purchaseForm.getRawValue();
    this.submitting.set(true);
    this.error.set(null);
    this.creditCardsService
      .createPurchase(this.card().id, {
        amount: amount ?? 0,
        description: description ?? '',
        purchaseDate: purchaseDate ?? ''
      })
      .subscribe({
        next: () => {
          this.submitting.set(false);
          this.purchaseForm.reset({
            amount: null,
            description: '',
            purchaseDate: this.today()
          });
          this.created.emit();
        },
        error: () => {
          this.submitting.set(false);
          this.error.set('No se pudo registrar la compra.');
        }
      });
  }

  submitPayment(): void {
    if (this.paymentForm.invalid) {
      this.paymentForm.markAllAsTouched();
      return;
    }
    const { amount, paymentDate } = this.paymentForm.getRawValue();
    this.submitting.set(true);
    this.error.set(null);
    this.creditCardsService
      .createPayment(this.card().id, {
        amount: amount ?? 0,
        paymentDate: paymentDate ?? ''
      })
      .subscribe({
        next: () => {
          this.submitting.set(false);
          this.paymentForm.reset({
            amount: null,
            paymentDate: this.today()
          });
          this.created.emit();
        },
        error: () => {
          this.submitting.set(false);
          this.error.set('No se pudo registrar el pago.');
        }
      });
  }

  private today(): string {
    return new Date().toISOString().slice(0, 10);
  }
}
