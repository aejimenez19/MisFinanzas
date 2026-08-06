export interface MonthlyFlow {
  month: string;
  incomes: number;
  expenses: number;
}

export interface CardBalanceView {
  id: number;
  name: string;
  bank: string;
  pendingBalance: number;
  creditLimit: number;
  limitUsagePercent: number;
  paymentDay: number;
  nextPaymentDate: string;
}

export interface RecentMovement {
  id: number;
  type: 'INCOME' | 'EXPENSE';
  description: string;
  amount: number;
  categoryName: string;
  movementDate: string;
}

export interface DashboardSummary {
  availableBalance: number;
  monthIncomes: number;
  monthExpenses: number;
  generatedAt: string;
  cashflow: MonthlyFlow[];
  cards: CardBalanceView[];
  nextPaymentAmount: number;
  nextPaymentDate: string | null;
  daysUntilDue: number;
  recentMovements: RecentMovement[];
}