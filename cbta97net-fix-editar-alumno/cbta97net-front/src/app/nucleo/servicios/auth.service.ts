import { Injectable, signal, computed, inject } from '@angular/core';
import { TokenService } from './token.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { map, Observable, tap } from 'rxjs';
import { throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, of } from 'rxjs';

import { Router } from '@angular/router';

export interface LoginResponse {
    access_token: string;
    expires_in: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

    private URL_LOGIN = `${environment.apiUrl}/usuarios/login`;
    private URL_LOGOUT = `${environment.apiUrl}/usuarios/logout`;

    constructor(
        private readonly tokenService: TokenService,
        private readonly http: HttpClient,
        private router: Router
    ) { }

    iniciarSesion(id: number, contrasena: string) {
        return this.http.post<LoginResponse>(this.URL_LOGIN, { id: id, contrasena: contrasena }).pipe(
            tap(res => this.tokenService.setToken(res.access_token)),
            catchError((error: HttpErrorResponse) => {
                const serverErrorMessage = error.error?.message || 'Error de comunicación';
                return throwError(() => serverErrorMessage);
            })
        );
    }

    cerrarSesion(): Observable<{ message: string }> {
        return this.http.post<{ message: string }>(
            this.URL_LOGOUT,
            {},
            { withCredentials: true }
        ).pipe(
            tap(() => {
                this.tokenService.removeToken();
                this.router.navigate(['/autenticacion', 'inicio-sesion']);
            }),
            catchError(error => {
                this.tokenService.removeToken();
                this.router.navigate(['/autenticacion', 'inicio-sesion']);
    
                return of({
                    message: error.error?.message || 'Error al intentar cerrar la sesión'
                });
            })
        );
    }
}