import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class MailService {
  private baseUrl = 'http://localhost:8080/';
  http = inject(HttpClient);

  public getInbox(
    userId: number,
    page: number,
    size: number,
    searchBy: string,
    filterBy: string,
    hasAttachment: boolean,
    priority: string,
    sortBy: string
  ) {
    return this.http.post(`${this.baseUrl}inbox`, {
      userId,
      page,
      size,
      searchBy,
      filterBy,
      hasAttachment,
      priority,
      sortBy,
    });
  }
  public getSent(
    userId: number,
    page: number,
    size: number,
    searchBy: string,
    filterBy: string,
    hasAttachment: boolean,
    priority: string,
    sortBy: string
  ) {
    return this.http.post(`${this.baseUrl}sent`, {
      userId,
      page,
      size,
      searchBy,
      filterBy,
      hasAttachment,
      priority,
      sortBy,
    });
  }
  public getInboxEmail(id: number) {
    return this.http.get(`${this.baseUrl}getInoxEmail/${id}`);
  }
  public getSentEmail(id: number) {
    return this.http.get(`${this.baseUrl}getSentEmail/${id}`);
  }
  public deleteMail(id: number) {
    return this.http.delete(`${this.baseUrl}deleteEmail/${id}`);
  }
  public bulkDelete(ids: number[]) {
    return this.http.put(`${this.baseUrl}bulkDelete`, ids);
  }

  // ! Send Email
  // attachment handling to be added later
  // Use clear parameter names for readability
  public sendEmail(userId: number, to: string[], subject: string, body: string, priority: string) {
    const payload = {
        userId,
        to,
        subject,
        body,
        priority
    };

    // CRITICAL FIX: Add { responseType: 'text' }
    return this.http.post(`${this.baseUrl}send`, payload, {
        responseType: 'text' as 'json' // Angular requires this type assertion for 'text'
    });
  }
}
