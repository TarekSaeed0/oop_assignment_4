import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {HomeComponent} from '../../components/home/home.component';
import {AuthenticationService} from '../authentication.service';
import {Attachment} from '../../types/mail';
import {Subject} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DraftsService {
  draftSubject = new Subject<number>()

  putDraft$ = this.draftSubject.asObservable()
  putDraft = (draftId: number) => {
    this.draftSubject.next(draftId)
  }

  private baseUrl = 'http://localhost:8080/api/drafts';
  authService = inject(AuthenticationService);
  http = inject(HttpClient);
  createDraft(body: string, subject: string, receivers: {email: string}[]
  , priority: string, attachments?: Attachment[]
  ) {
    return this.http.post(`${this.baseUrl}`, {
      body,
      subject,
      receivers,
      priority,
      attachments,
      userId: this.authService.user()?.id,
      senderName: this.authService.user()?.name,
      senderEmail: this.authService.user()?.email
    })
  }
  getDrafts() {
    return this.http.get(`${this.baseUrl}/user/${this.authService.user()?.id}`)
  }
  updateDraft(id: number, body: string, subject: string, receivers: {email: string}[]
    , priority: string, attachments?: Attachment[]
  ) {
    return this.http.put(`${this.baseUrl}/${id}`, {
      body,
      subject,
      receivers,
      priority,
      attachments,
      userId: this.authService.user()?.id,
      senderName: this.authService.user()?.name,
      senderEmail: this.authService.user()?.email
    })
  }
  getDraft(id: number) {
    return this.http.get(`${this.baseUrl}/draft/${id}`)
  }
  deleteDraft(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`, {
    responseType: 'text' 
  })
  }
}
