import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';
import { CarreraTecnica } from '@modelos/comunes/carrera-tecnica.model';

@Injectable({
    providedIn: 'root'
})
export class CarreraTecnicaServicio {

    private readonly URL_CARRERAS = `${environment.apiUrl}/carreras-tecnicas`;

    /**
     * Constructor por defecto. Permite asignar clientes HTTP mockeables para
     * realizacion de pruebas.
     * @param httpClient cliente para realizar peticiones HTTP.
     */
    constructor(private readonly http: HttpClient) { }

    /**
     * Recupera el listado completo de todas las carreras técnicas registradas.
     * @returns Un Observable con un arreglo de objetos CarreraTecnica.
     */
    obtenerTodasCarrerasTecnicas(): Observable<CarreraTecnica[]> {
        return this.http.get<CarreraTecnica[]>(this.URL_CARRERAS);
    }

    /**
     * Registra una nueva carrera técnica en el sistema.
     * @param carreraTecnica Objeto con los datos de la carrera a registrar.
     * @returns Un Observable con la carrera técnica creada y su ID.
     */
    registrarCarreraTecnica(carreraTecnica: CarreraTecnica): Observable<CarreraTecnica> {
        return this.http.post<CarreraTecnica>(this.URL_CARRERAS, carreraTecnica);
    }

    /**
     * Elimina una carrera técnica específica mediante su identificador.
     * @param id ID de la carrera técnica a eliminar.
     * @returns Un Observable con un objeto que contiene el mensaje de éxito.
     */
    eliminarCarreraTecnica(id: number): Observable<{ message: string }> {
        return this.http.delete<{ message: string }>(`${this.URL_CARRERAS}/${id}`);
    }

    /**
     * Busca y obtiene los detalles de una carrera técnica por su ID.
     * @param id ID de la carrera técnica.
     * @returns Un Observable con los datos de la carrera encontrada.
     */
    obtenerCarreraTecnicaPorId(id: number): Observable<CarreraTecnica> {
        return this.http.get<CarreraTecnica>(`${this.URL_CARRERAS}/${id}`);
    }
}