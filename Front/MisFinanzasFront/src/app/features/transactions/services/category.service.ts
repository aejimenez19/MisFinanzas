import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import type { Category, CategoryType } from '../models/category';

export const CATEGORY_API_BASE_URL = `${environment.apiUrl}/api/categories`;

@Injectable({ providedIn: 'root' })
export class CategoryService {
  constructor(private readonly http: HttpClient) {}

  list(type?: CategoryType): Observable<Category[]> {
    const params = type ? { type } : undefined;
    return this.http.get<Category[]>(CATEGORY_API_BASE_URL, { params });
  }
}
