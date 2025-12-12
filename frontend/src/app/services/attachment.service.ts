import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

interface Attachment {
  id: number;
  name: string;
  type: string;
  size: number;
}

@Injectable({
  providedIn: 'root',
})
export class AttachmentService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/attachments';

  uploadAttachments(files: File[]) {
    const formData = new FormData();
    for (let file of files) {
      formData.append('files', file);
    }
    return this.http.post<Attachment>(`${this.baseUrl}/upload`, formData, {
      withCredentials: true,
    });
  }

  downloadAttachment(id: number) {
    return this.http.get(`${this.baseUrl}/download/${id}`, {
      withCredentials: true,
      responseType: 'blob',
    });
  }

  getAttachmentUrl(id: number) {
    return `${this.baseUrl}/${id}`;
  }

  getAttachments() {
    return this.http.get<Attachment[]>(`${this.baseUrl}`, { withCredentials: true });
  }

  getAttachment(id: number) {
    return this.http.get<Attachment>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  deleteAttachment(id: number) {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }
}
