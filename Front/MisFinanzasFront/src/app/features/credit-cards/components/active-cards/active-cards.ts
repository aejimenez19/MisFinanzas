import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

import type { CreditCard } from '../../models/credit-card';

@Component({
  selector: 'app-active-cards',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './active-cards.html',
  styleUrl: './active-cards.css'
})
export class ActiveCards {
  readonly cards = input.required<CreditCard[]>();
  readonly selectedId = input<number | null>(null);
  readonly cardholderName = input('Titular');

  readonly select = output<number>();
  readonly edit = output<CreditCard>();

  isSelected(card: CreditCard): boolean {
    return card.id === this.selectedId();
  }

  maskedNumber(card: CreditCard): string {
    const last = card.lastFourDigits;
    const suffix = last ? last : String(card.id).padStart(4, '0').slice(-4);
    return `**** **** **** ${suffix}`;
  }
}
