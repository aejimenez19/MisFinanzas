import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';

import { AuthService } from '../../../auth/services/auth.service';
import type { CreditCard, CreditCardActivityItem } from '../../models/credit-card';
import { CreditCardsService } from '../../services/credit-cards.service';
import { ActiveCards } from '../../components/active-cards/active-cards';
import { CardActivity } from '../../components/card-activity/card-activity';
import { CardDetails } from '../../components/card-details/card-details';
import { CardFormDialog } from '../../components/card-form-dialog/card-form-dialog';
import { DebtSummary } from '../../components/debt-summary/debt-summary';
import { QuickActions } from '../../components/quick-actions/quick-actions';

@Component({
  selector: 'app-credit-cards',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './credit-cards.html',
  styleUrl: './credit-cards.css',
  imports: [ActiveCards, CardActivity, CardDetails, CardFormDialog, DebtSummary, QuickActions]
})
export class CreditCards {
  private readonly creditCardsService = inject(CreditCardsService);
  private readonly authService = inject(AuthService);

  readonly cards = signal<CreditCard[]>([]);
  readonly selectedId = signal<number | null>(null);
  readonly loading = signal(true);
  readonly loadError = signal<string | null>(null);

  readonly activity = signal<CreditCardActivityItem[]>([]);
  readonly activityLoading = signal(false);

  readonly dialogOpen = signal(false);
  readonly editingCard = signal<CreditCard | null>(null);

  readonly selectedCard = computed(() =>
    this.cards().find((card) => card.id === this.selectedId()) ?? null
  );

  readonly cardholderName = computed(() => {
    const user = this.authService.currentUser();
    return user ? `${user.firstName} ${user.lastName}`.trim() : 'Titular';
  });

  constructor() {
    this.loadCards();
  }

  selectCard(id: number): void {
    if (this.selectedId() === id) {
      return;
    }
    this.selectedId.set(id);
    this.loadActivity(id);
  }

  openCreateDialog(): void {
    this.editingCard.set(null);
    this.dialogOpen.set(true);
  }

  openEditDialog(card: CreditCard): void {
    this.editingCard.set(card);
    this.dialogOpen.set(true);
  }

  closeDialog(): void {
    this.dialogOpen.set(false);
    this.editingCard.set(null);
  }

  onCardSaved(card: CreditCard): void {
    this.cards.update((current) => {
      const exists = current.some((item) => item.id === card.id);
      const next = exists
        ? current.map((item) => (item.id === card.id ? card : item))
        : [...current, card];
      return next.sort((a, b) => a.name.localeCompare(b.name));
    });
    this.selectedId.set(card.id);
    this.loadActivity(card.id);
    this.dialogOpen.set(false);
    this.editingCard.set(null);
  }

  onCardDeleted(id: number): void {
    this.cards.update((current) => current.filter((card) => card.id !== id));
    this.activity.set([]);
    if (this.selectedId() === id) {
      this.selectedId.set(this.cards()[0]?.id ?? null);
      if (this.cards()[0]) {
        this.loadActivity(this.cards()[0].id);
      }
    }
    this.dialogOpen.set(false);
    this.editingCard.set(null);
  }

  onActivityChanged(): void {
    this.loadCards();
    const selected = this.selectedCard();
    if (selected) {
      this.loadActivity(selected.id);
    }
  }

  loadCards(): void {
    this.loading.set(true);
    this.loadError.set(null);
    this.creditCardsService
      .list()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (cards) => {
          this.cards.set(cards);
          const nextId = this.selectedId() ?? cards[0]?.id ?? null;
          this.selectedId.set(nextId);
          if (nextId !== null) {
            this.loadActivity(nextId);
          } else {
            this.activity.set([]);
          }
        },
        error: () => {
          this.cards.set([]);
          this.loadError.set('No se pudieron cargar las tarjetas.');
        }
      });
  }

  private loadActivity(cardId: number): void {
    this.activityLoading.set(true);
    this.creditCardsService
      .activity(cardId)
      .pipe(finalize(() => this.activityLoading.set(false)))
      .subscribe({
        next: (items) => this.activity.set(items),
        error: () => this.activity.set([])
      });
  }
}
