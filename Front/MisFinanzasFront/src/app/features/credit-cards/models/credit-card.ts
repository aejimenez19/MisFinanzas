export type CreditCardStatus = 'ACTIVE' | 'INACTIVE';

export interface CreditCardStatement {
  cutoffDate: string;
  dueDate: string;
  totalAmount: number;
  paidAmount: number;
  remainingAmount: number;
}

export interface CreditCard {
  id: number;
  name: string;
  bank: string;
  lastFourDigits?: string;
  creditLimit: number;
  cutoffDay: number;
  paymentDay: number;
  status: CreditCardStatus;
  pendingBalance: number;
  available: number;
  limitUsagePercent: number;
  nextPaymentDate: string;
  billedAmount: number;
  unbilledAmount: number;
  statements: CreditCardStatement[];
}

export interface CreditCardActivityItem {
  id: number;
  type: 'PURCHASE' | 'PAYMENT';
  description: string;
  amount: number;
  date: string;
  billingCycle?: string;
}

export interface CreateCardPayload {
  name: string;
  bank: string;
  lastFourDigits?: string;
  creditLimit: number;
  cutoffDay: number;
  paymentDay: number;
}

export interface CreatePurchasePayload {
  description: string;
  amount: number;
  purchaseDate: string;
}

export interface CreatePaymentPayload {
  amount: number;
  paymentDate: string;
}
