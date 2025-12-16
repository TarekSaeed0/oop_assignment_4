import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { DeletedMailDTO } from '../types/trash';

@Injectable({
  providedIn: 'root'
})
export class TrashService {
  private baseUrl = 'http://localhost:8080/';
  http = inject(HttpClient);

  getTrash(
    userId: number,
    page: number,
    size: number,
    searchBy: string,
    filterBy: string,
    hasAttachment: boolean,
    priority: string,
    sortBy: string
  ) {
    return this.http.post<DeletedMailDTO[]>(`${this.baseUrl}trash`, {
      userId,
      page,
      size,
      searchBy,
      filterBy,
      hasAttachment,
      priority,
      sortBy
    });
  }

  bulkRestore(ids: number[]) {
    return this.http.put(`${this.baseUrl}trash/restore/bulk`, ids);
  }

  bulkDeleteForever(ids: number[]) {
    // Note: 'body' is required for DELETE requests sending data
    return this.http.delete(`${this.baseUrl}trash/delete/bulk`, { body: ids });
  }
}