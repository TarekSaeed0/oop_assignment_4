import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { catchError, Observable, of, tap } from 'rxjs';

export interface SignupRequest {
  email: string;
  password: string;
  name: string;
}

export interface SigninRequest {
  email: string;
  password: string;
}

export interface User {
  id: number;
  email: string;
  name: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/auth';

  user = signal<User | null>(null);

  loadUser(): Observable<User | null> {
    return this.http.get<User>(`${this.baseUrl}/me`, { withCredentials: true }).pipe(
      tap((user) => this.user.set(user)),
      catchError(() => {
        this.user.set(null);
        return of(null);
      }),
    );
  }

  isAuthenticated = computed(() => this.user() !== null);

  signup(request: SignupRequest): Observable<Object> {
    return this.http.post(`${this.baseUrl}/signup`, request);
  }

  signin(request: SigninRequest): Observable<User> {
    return this.http
      .post<User>(`${this.baseUrl}/signin`, request, { withCredentials: true })
      .pipe(tap((user) => this.user.set(user)));
  }

  signout(): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/signout`, {}, { withCredentials: true }).pipe(
      tap(() => {
        this.user.set(null);
      }),
    );
  }
}
