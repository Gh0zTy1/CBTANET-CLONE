import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from 'environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private readonly TOKEN_KEY = 'accessToken';
  private readonly URL_USUARIOS = `${environment.apiUrl}/usuarios`;

  constructor(private readonly http: HttpClient, private readonly router: Router) {}


  // Store token securely
  public setToken(token: string): void {
    sessionStorage.setItem(this.TOKEN_KEY, token);
  }
  // Retrieve token
  public getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }
  // Remove token
  public removeToken(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
  }

  /**
   * Refresca el token cada vez que el access_token vence.
   * @returns access_token nuevo.
   */
  public refreshToken(): Observable<{ access_token: string }> {
    return this.http.post<{ access_token: string }>(
      `${this.URL_USUARIOS}/refresh-session`,
      {},
      { withCredentials: true }
    ).pipe(
      map(res => {
        this.setToken(res.access_token);
        return res;
      }),
      catchError(err => {
        this.removeToken();

        this.router.navigate(['/autenticacion', '/login']);

        return throwError(() => err);
      })
    );
  }

  private handleError(error: HttpErrorResponse) {
    const serverMessage = error.error?.error || 'Error desconocido';
    const errorDetail = `${serverMessage}`;

    console.error('Server Exception:', error);
    return throwError(() => new Error(errorDetail));
  }
}