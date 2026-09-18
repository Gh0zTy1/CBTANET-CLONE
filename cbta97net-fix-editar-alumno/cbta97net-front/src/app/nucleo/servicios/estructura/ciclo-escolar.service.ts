
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';
import { CicloEscolar } from '@modelos/comunes/ciclo-escolar.model';

@Injectable({
    providedIn: 'root'
})
export class CicloEscolarServicio {

    private readonly URL_CICLOS = `${environment.apiUrl}/ciclos-escolares`;

    /**
     * Constructor por defecto. Permite asignar clientes HTTP mockeables para
     * realizacion de pruebas.
     * @param httpClient cliente para realizar peticiones HTTP.
     */
    constructor(private readonly http: HttpClient) {}

    /**
     * Obtiene el ciclo escolar que se encuentra actualmente activo.
     * Corresponde al endpoint GET /ciclos-escolares/activo
     * @returns Un Observable con el CicloEscolar activo.
     */
    obtenerCicloEscolarActivo(): Observable<CicloEscolar> {
        return this.http.get<CicloEscolar>(`${this.URL_CICLOS}/activo`);
    }

    /**
     * Crea un nuevo ciclo escolar en el sistema.
     * Corresponde al endpoint POST /ciclos-escolares
     * @param cicloEscolar Objeto con los datos del nuevo ciclo (fechas inicio y fin).
     * @returns Un Observable con el ciclo escolar creado y su ID generado.
     */
    crearNuevoCicloEscolar(cicloEscolar: CicloEscolar): Observable<CicloEscolar> {
        return this.http.post<CicloEscolar>(this.URL_CICLOS, cicloEscolar);
    }
}