import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class MailService {
  private baseUrl = 'http://localhost:8080/';
  http = inject(HttpClient)

  public getInbox(userId: number, page: number, size: number, searchBy: string, filterBy: string, hasAttachment: boolean, priority: string) {
    return this.http.post(`${this.baseUrl}inbox`, {
      userId,
      page,
      size,
      searchBy,
      filterBy,
      hasAttachment,
      priority
    })
  }
  public getSent(userId: number, page: number, size: number, searchBy: string, filterBy: string, hasAttachment: boolean, priority: string) {
    return this.http.post(`${this.baseUrl}sent`, {
      userId,
      page,
      size,
      searchBy,
      filterBy,
      hasAttachment,
      priority
    })
  }
  public getEmail(id: number) {
    return this.http.get(`${this.baseUrl}getEmail/${id}`);
  }
}
