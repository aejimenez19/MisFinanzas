import type { CategoryType } from './category';

export interface Transaction {
  id: number;
  type: CategoryType;
  description: string;
  amount: number;
  categoryId: number;
  categoryName: string;
  movementDate: string;
}
