import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';
import { Aula } from '@modelos/comunes/aula.model';

@Injectable({
    providedIn: 'root'
})
export class AulaServicio {

    private readonly URL_AULAS = `${environment.apiUrl}/aulas`;

    /**
     * Constructor por defecto. Permite asignar clientes HTTP mockeables para
     * realizacion de pruebas.
     * @param httpClient cliente para realizar peticiones HTTP.
     */
    constructor(private readonly http: HttpClient) { }

    /**
     * Registra una nueva aula en el sistema.
     * Requiere rol: AULAS_CREATE
     * @param aula Objeto con la clave y datos del aula.
     * @returns Un Observable con el aula creada (incluyendo su ID).
     */
    registrarAula(aula: Aula): Observable<Aula> {
        return this.http.post<Aula>(this.URL_AULAS, aula);
    }

    /**
     * Elimina un aula mediante su identificador numérico.
     * Requiere rol: AULAS_DELETE
     * @param id ID del aula a eliminar.
     * @returns Un Observable con el mensaje de confirmación.
     */
    eliminarAulaPorId(id: number): Observable<{ message: string }> {
        return this.http.delete<{ message: string }>(`${this.URL_AULAS}/${id}`);
    }

    /**
     * Elimina un aula utilizando su clave única.
     * Requiere rol: AULAS_DELETE
     * @param clave Clave alfanumérica del aula.
     * @returns Un Observable con el mensaje de confirmación.
     */
    eliminarAulaPorClave(clave: string): Observable<{ message: string }> {
        return this.http.delete<{ message: string }>(`${this.URL_AULAS}/clave/${clave}`);
    }

    /**
     * Recupera el listado completo de aulas registradas.
     * @returns Un Observable con un arreglo de objetos Aula.
     */
    obtenerTodasLasAulas(): Observable<Aula[]> {
        return this.http.get<Aula[]>(this.URL_AULAS);
    }
}