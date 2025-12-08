import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private baseUrl = 'http://localhost:8080/api/contact/';
  http = inject(HttpClient)


  // returns the list of contacts for a given user
  public getContacts(userId: number) {
    return this.http.get(`${this.baseUrl}user/${userId}/contacts`);
  }

  // creates a new contact for a given user
  public createContact(userId: number, name: string, contactEmails: string[]) {
    return this.http.post(`${this.baseUrl}createContact`, {
      name,
      contactEmails,
      userId
    });
  }

  // deletes a contact by its ID
  public deleteContact(contactId: number) {
    return this.http.delete(`${this.baseUrl}deleteContact/${contactId}`,
    { responseType: 'text' });
  }

  public editContact(userId: number, contactId: number, name: string, contactEmails: string[]) {
    return this.http.put(`${this.baseUrl}editContact/${contactId}`, {
      name,
      contactEmails,
      userId
    });
  }
}
