import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

// --- Interfaces (DTOs) ---
// These match the structure expected by your Spring Boot @RequestBody

export interface FolderRequest {
  name: string;
  id: number;
}

export interface MailToFolderRequest {
  userId: number;
  folderName: string;
  mailIds: number[];
}

export interface UserFolder {
  // Update these fields to match your Java 'UserFolder' model
  id?: number;
  name?: string;
  // any other fields...
}

@Injectable({
  providedIn: 'root'
})
export class UserFolderService {
  
  // Matches @RequestMapping("/UserFolder")
  private baseUrl = 'http://localhost:8080/UserFolder'; 
  
  private http = inject(HttpClient);

  // --- GET Methods ---

  // Endpoint: @GetMapping("/isValidName")
  // Java Params: @RequestParam String name, @RequestParam Long id
  public isValidNameForUserFolder(name: string, userId: number): Observable<boolean> {
    const params = new HttpParams()
      .set('name', name)
      .set('id', userId);

    return this.http.get<boolean>(`${this.baseUrl}/isValidName`, { params });
  }

  // Endpoint: @GetMapping("getUserFolders")
  // Java Params: @RequestParam Long userId
  public getUserFolders(userId: number): Observable<UserFolder[]> {
    const params = new HttpParams().set('userId', userId);
    
    return this.http.get<UserFolder[]>(`${this.baseUrl}/getUserFolders`, { params });
  }

  // --- POST Methods ---

  // Endpoint: @PostMapping("/createFolder")
  // Java Body: FolderRequest (name, id)
  public createFolder(folderName: string, userId: number): Observable<string> {
    const body: FolderRequest = { 
      name: folderName, 
      id: userId 
    };

    // Using responseType: 'text' because the backend returns void (empty body)
    // If we expect JSON, Angular will throw a parsing error on an empty response.
    return this.http.post(`${this.baseUrl}/createFolder`, body, { responseType: 'text' });
  }

  // Endpoint: @PostMapping("/addMailToFolder")
  // Java Body: MailToFolderRequest (userId, folderName, mailIds)
  public addToFolder(userId: number, folderName: string, mailIds: number[]): Observable<string> {
    const body: MailToFolderRequest = { 
      userId, 
      folderName, 
      mailIds 
    };

    return this.http.post(`${this.baseUrl}/addMailToFolder`, body, { responseType: 'text' });
  }

  // Endpoint: @PostMapping("/deleteMailFromFolder")
  // Java Body: MailToFolderRequest (userId, folderName, mailIds)
  public deleteFromFolder(userId: number, folderName: string, mailIds: number[]): Observable<string> {
    const body: MailToFolderRequest = { 
      userId, 
      folderName, 
      mailIds 
    };

    return this.http.post(`${this.baseUrl}/deleteMailFromFolder`, body, { responseType: 'text' });
  }

public deleteFolder(userId: number, folderName: string): Observable<string> {
    const params = new HttpParams().set('id', userId);
    
    // The backend expects a body with the name
    const body: FolderRequest = { 
      name: folderName, 
      id: userId 
    };

    return this.http.post(`${this.baseUrl}/deleteFolder`, body, { 
        params, 
        responseType: 'text' 
    });
  }

  public renameFolder(userId: number, oldName: string, newName: string): Observable<string> {
    const params = new HttpParams()
        .set('id', userId)
        .set('newName', newName);

    // The backend uses the body to identify the OLD folder
    const body: FolderRequest = { 
      name: oldName, 
      id: userId 
    };

    return this.http.post(`${this.baseUrl}/renameFolder`, body, { 
        params, 
        responseType: 'text' 
    });
  }

}