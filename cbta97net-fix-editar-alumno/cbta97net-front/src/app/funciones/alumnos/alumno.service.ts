import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { Observable, map, catchError, throwError } from 'rxjs';
import { environment } from 'environments/environment';
import { InformacionBasicaAlumno } from '../../nucleo/modelos/alumnos/informacion-basica-alumno.model';
import { Pagina } from "@nucleo/util/paginacion/pagina.model";
import { Alumno } from '../../nucleo/modelos/alumnos/alumno.model';
import { AlumnoGuardar } from '../../nucleo/modelos/alumnos/alumno-guardar.model';

@Injectable({ providedIn: 'root' })
export class AlumnoServicio {

    private readonly URL_ALUMNOS = `${environment.apiUrl}/alumnos`;

    /**
     * Constructor por defecto. Permite asignar clientes HTTP mockeables para
     * realizacion de pruebas.
     * @param httpClient cliente para realizar peticiones HTTP.
     */
    constructor(private readonly httpClient: HttpClient) { }

    /**
     * Registra un alumno con su información básica en el sistema, datos como matrícula, nombre, 
     * apellidos, etc. Además de la información de su tutor legal (nombre, apellido, teléfono, etc).
     * @param alumno Información del Alumno.
     */
    registrarAlumno(alumno: AlumnoGuardar): Observable<void> {
        return this.httpClient.post<void>(this.URL_ALUMNOS, alumno, { observe: 'response' }).pipe(
            map(response => {
                if (response.status === HttpStatusCode.Created) return;
            }),
            catchError(this.handleError)
        );
    }

    /**
     * Actualiza la información de un alumno existente en el sistema.
     * @param matricula Matrícula del alumno a actualizar.
     * @param alumno Datos actualizados del alumno.
     */
    actualizarAlumno(matricula: string, alumno: AlumnoGuardar): Observable<void> {
        return this.httpClient.put<void>(`${this.URL_ALUMNOS}/${matricula}`, alumno, { observe: 'response' }).pipe(
            map(response => {
                if (response.status === HttpStatusCode.Ok) return;
            }),
            catchError(this.handleError)
        );
    }


    /**
     * Obtiene la información completa de un usuario a través de su matrícula.
     * @param matricula Matrícula del alumno.
     * @returns Alumno encontrado.
     */
    obtenerAlumnoPorMatricula(matricula: string): Observable<Alumno> {
        return this.httpClient.get<Alumno>(`${this.URL_ALUMNOS}/${matricula}`).pipe(catchError(this.handleError));
    }

    /**
     * Busca alumnos de forma paginada basándose en sus credenciales (nombre, matrícula, CURP, etc).
     * @param busqueda Texto a buscar.
     * @param page Número de página (empieza en 0).
     * @param size Cantidad de registros por página.
     * @returns Observable con la estructura de página y el contenido de información básica.
     */
    buscarAlumnosPorCredenciales(busqueda: string, page: number = 0, size: number = 10): Observable<Pagina<InformacionBasicaAlumno>> {

        const params = {
            credenciales: busqueda,
            page: page.toString(),
            size: size.toString()
        };

        return this.httpClient.get<Pagina<InformacionBasicaAlumno>>(
            `${this.URL_ALUMNOS}/buscar`,
            { params }
        ).pipe(
            catchError(this.handleError)
        );
    }

    /**
     * Eliminar un alumno a través de su matrícula.
     * @param matricula Matrícula del alumno.
     * @returns string Mensaje del servidor.
     */
    eliminarAlumnoPorMatricula(matricula: string): Observable<string> {
        return this.httpClient.delete<{ message: string }>(`${this.URL_ALUMNOS}/${matricula}`).pipe(
            map(res => {
                return res.message;
            }),
            catchError(this.handleError)
        );
    }

    /**
     * Guarda el documento de Acta de Nacimiento del alumno a través de su matrícula.
     * @param matricula Matrícula del alumno.
     * @param archivo Documento de Acta de Nacimiento.
     * @returns Mensaje de exito del servidor.
     */
    guardarActaNacimiento(matricula: string, archivo: File): Observable<string> {
        const formData = new FormData();

        formData.append('documento', archivo);

        return this.httpClient.post<{ message: string }>(`${this.URL_ALUMNOS}/${matricula}/documentos/acta-nacimiento`, formData, {
            observe: 'response'
        }).pipe(
            map(res => {
                if (res.status === HttpStatusCode.Ok || res.status == HttpStatusCode.Created) {
                    return res.body?.message || "Archivo guardado correctamente.";
                }

                throw new Error('Respuesta inesperada del servidor');
            }),
            catchError(this.handleError)
        );
    }

    /**
     * Guarda el documento CURP del alumno a través de su matrícula.
     * @param matricula Matrícula del alumno.
     * @param archivo Documento del CURP.
     * @returns Mensaje de exito del servidor.
     */
    guardarDocumentoCURP(matricula: string, archivo: File): Observable<string> {
        const formData = new FormData();

        formData.append('documento', archivo);

        return this.httpClient.post<{ message: string }>(`${this.URL_ALUMNOS}/${matricula}/documentos/curp`, formData, {
            observe: 'response'
        }).pipe(
            map(res => {
                if (res.status === HttpStatusCode.Ok || res.status == HttpStatusCode.Created) {
                    return res.body?.message || "Archivo guardado correctamente.";
                }

                throw new Error('Respuesta inesperada del servidor');
            }),
            catchError(this.handleError)
        );
    }

    /**
     * Guarda el documento de Certificado de Secundaria del alumno a través de su matrícula.
     * @param matricula Matricula del alumno.
     * @param archivo Documento del Certificado de Secundaria.
     * @returns Mensaje de exito del servidor.
     */
    guardarCertificadoSecundaria(matricula: string, archivo: File): Observable<string> {
        const formData = new FormData();

        formData.append('documento', archivo);

        return this.httpClient.post<{ message: string }>(`${this.URL_ALUMNOS}/${matricula}/documentos/certificado-secundaria`, formData, {
            observe: 'response'
        }).pipe(
            map(res => {
                if (res.status === HttpStatusCode.Ok || res.status == HttpStatusCode.Created) {
                    return res.body?.message || "Archivo guardado correctamente.";
                }

                throw new Error('Respuesta inesperada del servidor');
            }),
            catchError(this.handleError)
        );
    }

    /**
     * Guarda la foto escolar del alumno a través de su matrícula.
     * @param matricula Matrícula del alumno.
     * @param archivo Foto escolar del alumno (.jpeg, .jpg, .png, etc)
     * @returns Mensaje de exito del servidor.
     */
    guardarFotoEscolar(matricula: string, archivo: File): Observable<string> {
        const formData = new FormData();

        formData.append('documento', archivo);

        return this.httpClient.post<{ message: string }>(`${this.URL_ALUMNOS}/${matricula}/documentos/foto`, formData, {
            observe: 'response'
        }).pipe(
            map(res => {
                if (res.status === HttpStatusCode.Ok || res.status == HttpStatusCode.Created) {
                    return res.body?.message || "Archivo guardado correctamente.";
                }

                throw new Error('Respuesta inesperada del servidor');
            }),
            catchError(this.handleError)
        );
    }

    /**
     * Obtiene el acta de nacimiento del alumno en formato Blob (PDF).
     * @param matricula Matrícula del alumno.
     */
    obtenerActaNacimiento(matricula: string): Observable<Blob> {
        return this.httpClient.get(`${this.URL_ALUMNOS}/${matricula}/documentos/acta-nacimiento`, {
            responseType: 'blob'
        }).pipe(
            catchError(this.handleError)
        );
    }

    /**
     * Obtiene el documento CURP del alumno en formato Blob (PDF).
     * @param matricula Matrícula del alumno.
     */
    obtenerDocumentoCURP(matricula: string): Observable<Blob> {
        return this.httpClient.get(`${this.URL_ALUMNOS}/${matricula}/documentos/curp`, {
            responseType: 'blob'
        }).pipe(
            catchError(this.handleError)
        );
    }

    /**
     * Obtiene el certificado de secundaria del alumno en formato Blob (PDF).
     * @param matricula Matrícula del alumno.
     */
    obtenerCertificadoSecundaria(matricula: string): Observable<Blob> {
        return this.httpClient.get(`${this.URL_ALUMNOS}/${matricula}/documentos/certificado-secundaria`, {
            responseType: 'blob'
        }).pipe(
            catchError(this.handleError)
        );
    }

    /**
     * Obtiene la foto escolar del alumno en formato Blob.
     * @param matricula Matrícula del alumno.
     */
    obtenerFotoEscolar(matricula: string): Observable<Blob> {
        return this.httpClient.get(`${this.URL_ALUMNOS}/${matricula}/documentos/foto`, {
            responseType: 'blob'
        }).pipe(
            catchError(this.handleError)
        );
    }


    importarAlumnosSiseems(archivo: File): Observable<{ message: string }> {
        const formData = new FormData();
        formData.append('documento', archivo);

        return this.httpClient.post<{ message: string }>(`${this.URL_ALUMNOS}/importar`, formData, {
            observe: 'response'
        }).pipe(
            map(res => {
                if (res.status === HttpStatusCode.Ok || res.status === HttpStatusCode.Created) {
                    return {
                        message: res.body?.message || "Se importaron los alumnos correctamente."
                    };
                }

                throw new Error('Respuesta inesperada del servidor');
            }),
            catchError(this.handleError)
        );
    }

    private handleError(error: HttpErrorResponse) {
        // Extraemos el mensaje que viene del backend
        const serverMessage = error.error?.error || 'Error desconocido, revise su conexion, si persiste comuniquese con su provedor';
        const errorDetail = `${serverMessage}`;

        return throwError(() => new Error(errorDetail));
    }
}