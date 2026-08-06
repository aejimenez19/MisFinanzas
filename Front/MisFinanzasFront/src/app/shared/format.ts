import type { CategoryType } from '../features/transactions/models/category';

const currency = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD'
});

const percent = new Intl.NumberFormat('en-US', {
  style: 'percent',
  maximumFractionDigits: 1
});

const dateFormatter = new Intl.DateTimeFormat('es-ES', {
  day: 'numeric',
  month: 'short',
  year: 'numeric'
});

const shortDateFormatter = new Intl.DateTimeFormat('es-ES', {
  day: 'numeric',
  month: 'short'
});

const shortMonthFormatter = new Intl.DateTimeFormat('es-ES', {
  month: 'short'
});

const iconByCategory: Record<string, string> = {
  groceries: 'shopping_cart',
  rent: 'home',
  shopping: 'shopping_bag',
  entertainment: 'movie',
  salary: 'work',
  investments: 'trending_up',
  gift: 'redeem',
  extra: 'payments'
};

export function formatCurrency(amount: number): string {
  return currency.format(amount);
}

export function formatAmount(amount: number, type: CategoryType): string {
  return currency.format(type === 'EXPENSE' ? -amount : amount);
}

export function formatPercent(value: number): string {
  return percent.format(value / 100);
}

export function formatDate(date: string): string {
  return dateFormatter.format(new Date(`${date}T00:00:00`));
}

export function formatShortDate(date: string): string {
  return shortDateFormatter.format(new Date(`${date}T00:00:00`));
}

export function formatMonthLabel(month: string): string {
  const [year, monthIndex] = month.split('-').map(Number);
  return shortMonthFormatter.format(new Date(year, monthIndex - 1, 1));
}

export function categoryIcon(categoryName: string): string {
  return iconByCategory[categoryName.toLowerCase()] ?? 'payments';
}