import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Attachment } from '../types/mail';

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
    sortBy: string,
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
    sortBy: string,
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
    return this.http.get(`${this.baseUrl}getInboxEmail/${id}`);
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

  public getMailsByFolder(userId: number, folderName: string) {
    return this.http.get<any[]>(`${this.baseUrl}folder/${folderName}`, {
      params: {
        userId: userId.toString(),
      },
    });
  }
  

  // ! Send Email
  // Use clear parameter names for readability
  public sendEmail(
    userId: number,
    to: string[],
    subject: string,
    body: string,
    priority: string,
    attachments: Attachment[],
  ) {
    const payload = {
      userId,
      to,
      subject,
      body,
      priority,
      attachments,
    };

    // CRITICAL FIX: Add { responseType: 'text' }
    return this.http.post(`${this.baseUrl}send`, payload, {
      responseType: 'text' as 'json', // Angular requires this type assertion for 'text'
    });
  }

  public isValidEmail(userId: number, to: string[], subject: string, body: string, priority: string) {
    const payload = {
        userId,
        to,
        subject,
        body,
        priority
    };

    // CRITICAL FIX: Add { responseType: 'text' }
    return this.http.post(`${this.baseUrl}check`, payload, {
        responseType: 'text' as 'json' // Angular requires this type assertion for 'text'
    });
  }
}
