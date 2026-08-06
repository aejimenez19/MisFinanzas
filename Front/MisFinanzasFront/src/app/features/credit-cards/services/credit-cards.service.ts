import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import type {
  CreateCardPayload,
  CreatePaymentPayload,
  CreatePurchasePayload,
  CreditCard,
  CreditCardActivityItem,
  CreditCardStatement
} from '../models/credit-card';

interface CreditCardStatementResponse {
  cutoffDate: string;
  dueDate: string;
  totalAmount: number;
  paidAmount: number;
  remainingAmount: number;
}

interface CreditCardResponse {
  id: number;
  name: string;
  bank: string;
  lastFourDigits?: string;
  creditLimit: number;
  cutoffDay: number;
  paymentDay: number;
  status: 'ACTIVE' | 'INACTIVE';
  pendingBalance: number;
  limitUsagePercent: number;
  nextPaymentDate: string;
  billedAmount: number;
  unbilledAmount: number;
  statements: CreditCardStatementResponse[];
}

@Injectable({ providedIn: 'root' })
export class CreditCardsService {
  private readonly baseUrl = `${environment.apiUrl}/api/credit-cards`;

  constructor(private readonly http: HttpClient) {}

  list(): Observable<CreditCard[]> {
    return this.http.get<CreditCardResponse[]>(this.baseUrl).pipe(
      map((cards) => cards.map((card) => this.toModel(card)))
    );
  }

  create(payload: CreateCardPayload): Observable<CreditCard> {
    return this.http.post<CreditCardResponse>(this.baseUrl, payload).pipe(
      map((card) => this.toModel(card))
    );
  }

  update(id: number, payload: CreateCardPayload): Observable<CreditCard> {
    return this.http.put<CreditCardResponse>(`${this.baseUrl}/${id}`, payload).pipe(
      map((card) => this.toModel(card))
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  activity(cardId: number): Observable<CreditCardActivityItem[]> {
    return this.http.get<CreditCardActivityItem[]>(`${this.baseUrl}/${cardId}/activity`);
  }

  createPurchase(cardId: number, payload: CreatePurchasePayload): Observable<unknown> {
    return this.http.post(`${this.baseUrl}/${cardId}/purchases`, payload);
  }

  createPayment(cardId: number, payload: CreatePaymentPayload): Observable<unknown> {
    return this.http.post(`${this.baseUrl}/${cardId}/payments`, payload);
  }

  private toModel(card: CreditCardResponse): CreditCard {
    return {
      id: card.id,
      name: card.name,
      bank: card.bank,
      lastFourDigits: card.lastFourDigits,
      creditLimit: card.creditLimit,
      cutoffDay: card.cutoffDay,
      paymentDay: card.paymentDay,
      status: card.status,
      pendingBalance: card.pendingBalance,
      available: card.creditLimit - card.pendingBalance,
      limitUsagePercent: card.limitUsagePercent,
      nextPaymentDate: card.nextPaymentDate,
      billedAmount: card.billedAmount ?? 0,
      unbilledAmount: card.unbilledAmount ?? 0,
      statements: (card.statements ?? []).map((statement) => this.toStatementModel(statement))
    };
  }

  private toStatementModel(statement: CreditCardStatementResponse): CreditCardStatement {
    return {
      cutoffDate: statement.cutoffDate,
      dueDate: statement.dueDate,
      totalAmount: statement.totalAmount,
      paidAmount: statement.paidAmount,
      remainingAmount: statement.remainingAmount
    };
  }
}
