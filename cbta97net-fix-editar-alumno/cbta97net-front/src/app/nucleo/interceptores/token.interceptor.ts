import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take } from 'rxjs/operators';
import { TokenService } from '../servicios/token.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);

  constructor(private tokenService: TokenService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.tokenService.getToken();
    
    // Simplificamos la clonación inicial
    const authReq = token ? this.addToken(req, token) : req;

    return next.handle(authReq).pipe(
      catchError((error) => {
        if (this.shouldRefresh(error, authReq)) {
          return this.handle401Error(authReq, next);
        }
        return throwError(() => error);
      })
    );
  }

  private shouldRefresh(error: any, req: HttpRequest<any>): boolean {
    return error instanceof HttpErrorResponse && 
           error.status === 401 && 
           !req.url.includes('/login') && !req.url.includes('/refresh-session');
  }

  /**
   * Maneja el proceso de refresco de token en caso de que la respuesta de una 
   * llamada sea respondida con código 401 (Unauthorized).
   * @param request Objeto de solicitud POST.
   * @param next Callback.
   * @returns 
   */
  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
  if (!this.isRefreshing) {
    this.isRefreshing = true;
    this.refreshTokenSubject.next(null); // Limpiamos el estado anterior

    return this.tokenService.refreshToken().pipe(
      switchMap((res) => {
        this.isRefreshing = false;
        // Emitimos el nuevo token a todos los que estaban esperando
        this.refreshTokenSubject.next(res.access_token); 
        return next.handle(this.addToken(request, res.access_token));
      }),
      catchError((err) => {
        this.isRefreshing = false;
        // IMPORTANTE: Notificar error al subject para que no se queden colgados
        this.refreshTokenSubject.error(err); 
        this.tokenService.removeToken();
        return throwError(() => err);
      })
    );
  }

  // Las peticiones secundarias esperan aquí
  return this.refreshTokenSubject.pipe(
    filter(token => token !== null), // Espera hasta que el token no sea null
    take(1), // Toma el primero y cierra la suscripción (evita fugas de memoria)
    switchMap(token => next.handle(this.addToken(request, token!)))
  );
}

  private addToken(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }
}