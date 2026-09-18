import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';
import { AreaPropedeutica } from '@modelos/comunes/area-propedeutica.model';

@Injectable({
    providedIn: 'root'
})
export class AreaPropedeuticaServicio {

    private readonly URL_AREAS = `${environment.apiUrl}/areas-propedeuticas`;

    /**
     * Constructor por defecto. Permite asignar clientes HTTP mockeables para
     * realizacion de pruebas.
     * @param httpClient cliente para realizar peticiones HTTP.
     */
    constructor(private readonly http: HttpClient) { }

    /**
     * Registra una nueva área propedéutica en el sistema.
     * Requiere rol: AREAS_PROPEDEUTICAS_CREATE
     * @param areaPropedeutica Objeto con los datos del área a crear.
     * @returns Un Observable con el área creada y su estado 201 Created.
     */
    crearAreaPropedeutica(areaPropedeutica: AreaPropedeutica): Observable<AreaPropedeutica> {
        return this.http.post<AreaPropedeutica>(this.URL_AREAS, areaPropedeutica);
    }

    /**
     * Recupera el listado completo de todas las áreas propedéuticas registradas.
     * @returns Un Observable con un arreglo de objetos AreaPropedeutica.
     */
    obtenerAreasPropedeuticas(): Observable<AreaPropedeutica[]> {
        return this.http.get<AreaPropedeutica[]>(this.URL_AREAS);
    }

    /**
     * Busca una área propedéutica específica por su identificador.
     * @param id ID del área propedéutica.
     * @returns Un Observable con los datos del área encontrada.
     */
    obtenerAreaPorId(id: number): Observable<AreaPropedeutica> {
        return this.http.get<AreaPropedeutica>(`${this.URL_AREAS}/${id}`);
    }

    /**
     * Elimina de forma lógica o física una área propedéutica del sistema.
     * Requiere rol: AREAS_PROPEDEUTICAS_DELETE
     * @param id ID del área a eliminar.
     * @returns Un Observable con un mensaje de confirmación del servidor.
     */
    eliminarAreaPropedeutica(id: number): Observable<{ message: string }> {
        return this.http.delete<{ message: string }>(`${this.URL_AREAS}/${id}`);
    }
}