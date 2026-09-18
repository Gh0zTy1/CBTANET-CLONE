import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';
import { Materia } from '@modelos/comunes/materia.model';

@Injectable({
    providedIn: 'root'
})
export class MateriaServicio {

    private readonly URL_MATERIAS = `${environment.apiUrl}/materias`;

    /**
     * Constructor por defecto. Permite asignar clientes HTTP mockeables para
     * realizacion de pruebas.
     * @param httpClient cliente para realizar peticiones HTTP.
     */
    constructor(private readonly http: HttpClient) {}

    /**
     * Obtiene una materia específica mediante su identificador único.
     * @param id El ID de la materia a buscar.
     * @returns Un Observable con el objeto Materia encontrado.
     */
    obtenerMateriaPorId(id: number): Observable<Materia> {
        return this.http.get<Materia>(`${this.URL_MATERIAS}/${id}`);
    }

    /**
     * Registra una nueva materia en el sistema.
     * @param materia Objeto con los datos de la materia a guardar.
     * @returns Un Observable con la materia creada y su ID asignado.
     */
    registrarMateria(materia: Materia): Observable<Materia> {
        return this.http.post<Materia>(this.URL_MATERIAS, materia);
    }

    /**
     * Recupera el listado completo de todas las materias registradas.
     * @returns Un Observable con un arreglo de objetos Materia.
     */
    obtenerTodasLasMaterias(): Observable<Materia[]> {
        return this.http.get<Materia[]>(this.URL_MATERIAS);
    }

    /**
     * Filtra y obtiene las materias pertenecientes a una carrera técnica específica.
     * @param carreraTecnicaId ID de la carrera técnica.
     * @returns Un Observable con el listado de materias de dicha carrera.
     */
    obtenerMateriasPorCarrera(carreraTecnicaId: number): Observable<Materia[]> {
        return this.http.get<Materia[]>(`${this.URL_MATERIAS}/carrera/${carreraTecnicaId}`);
    }

    /**
     * Recupera las materias asociadas a un área propedéutica específica.
     * @param id ID del área propedéutica.
     * @returns Un Observable con el listado de materias filtradas por área.
     */
    obtenerMateriasPorAreaPropedeutica(id: number): Observable<Materia[]> {
        return this.http.get<Materia[]>(`${this.URL_MATERIAS}/area/${id}`);
    }

    /**
     * Obtiene las materias correspondientes a un grado o semestre específico.
     * @param grado Número del semestre (Ej. 1 al 6).
     * @returns Un Observable con las materias impartidas en ese semestre.
     */
    obtenerMateriasPorGrado(grado: number): Observable<Materia[]> {
        return this.http.get<Materia[]>(`${this.URL_MATERIAS}/grado/${grado}`);
    }

    /**
     * Realiza una búsqueda filtrando simultáneamente por grado y carrera técnica.
     * @param grado Número del semestre.
     * @param carreraTecnicaId ID de la carrera técnica.
     * @returns Un Observable con las materias que coinciden con ambos criterios.
     */
    obtenerMateriasPorGradoYCarrera(grado: number, carreraTecnicaId: number): Observable<Materia[]> {
        return this.http.get<Materia[]>(`${this.URL_MATERIAS}/grado/${grado}/carrera/${carreraTecnicaId}`);
    }

    /**
     * Búsqueda avanzada de materias aplicando tres filtros: grado, carrera y área propedéutica.
     * @param grado Número del semestre.
     * @param carreraId ID de la carrera técnica.
     * @param areaId ID del área propedéutica.
     * @returns Un Observable con las materias que cumplen con los tres parámetros de búsqueda.
     */
    obtenerMateriasCompleto(grado: number, carreraId: number, areaId: number): Observable<Materia[]> {
        return this.http.get<Materia[]>(`${this.URL_MATERIAS}/grado/${grado}/carrera/${carreraId}/area/${areaId}`);
    }
}