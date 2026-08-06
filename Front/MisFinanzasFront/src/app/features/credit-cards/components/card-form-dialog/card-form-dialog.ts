import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  OnInit,
  output,
  signal
} from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import type { CreditCard } from '../../models/credit-card';
import { CreditCardsService } from '../../services/credit-cards.service';
import { CurrencyInputDirective } from '../../../../shared/currency-input.directive';

type Field = 'name' | 'bank' | 'lastFourDigits' | 'creditLimit' | 'cutoffDay' | 'paymentDay';

@Component({
  selector: 'app-card-form-dialog',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, CurrencyInputDirective],
  templateUrl: './card-form-dialog.html',
  styleUrl: './card-form-dialog.css'
})
export class CardFormDialog implements OnInit {
  readonly card = input<CreditCard | null>(null);

  readonly saved = output<CreditCard>();
  readonly deleted = output<number>();
  readonly closed = output<void>();

  private readonly creditCardsService = inject(CreditCardsService);

  readonly submitting = signal(false);
  readonly deleting = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = new FormGroup({
    name: new FormControl('', [Validators.required]),
    bank: new FormControl('', [Validators.required]),
    lastFourDigits: new FormControl('', [Validators.pattern(/^(?:\d{4})?$/)]),
    creditLimit: new FormControl<number | null>(null, [Validators.required, Validators.min(0.01)]),
    cutoffDay: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(1),
      Validators.max(31)
    ]),
    paymentDay: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(1),
      Validators.max(31)
    ])
  });

  ngOnInit(): void {
    const card = this.card();
    if (card) {
      this.form.patchValue({
        name: card.name,
        bank: card.bank,
        lastFourDigits: card.lastFourDigits ?? '',
        creditLimit: card.creditLimit,
        cutoffDay: card.cutoffDay,
        paymentDay: card.paymentDay
      });
    }
  }

  isEditing(): boolean {
    return this.card() !== null;
  }

  hasError(field: Field): boolean {
    const control = this.form.controls[field];
    return control.touched && control.invalid;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { name, bank, lastFourDigits, creditLimit, cutoffDay, paymentDay } = this.form.getRawValue();
    const payload = {
      name: name ?? '',
      bank: bank ?? '',
      lastFourDigits: lastFourDigits?.trim() || undefined,
      creditLimit: creditLimit ?? 0,
      cutoffDay: cutoffDay ?? 1,
      paymentDay: paymentDay ?? 1
    };

    const card = this.card();
    this.submitting.set(true);
    this.error.set(null);
    const operation = card
      ? this.creditCardsService.update(card.id, payload)
      : this.creditCardsService.create(payload);

    operation.subscribe({
      next: (result) => this.saved.emit(result),
      error: () => {
        this.submitting.set(false);
        this.error.set('No se pudo guardar la tarjeta.');
      }
    });
  }

  onDelete(): void {
    const card = this.card();
    if (!card) {
      return;
    }
    this.deleting.set(true);
    this.error.set(null);
    this.creditCardsService.delete(card.id).subscribe({
      next: () => this.deleted.emit(card.id),
      error: () => {
        this.deleting.set(false);
        this.error.set('No se pudo eliminar la tarjeta.');
      }
    });
  }
}