import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, map, catchError, throwError } from 'rxjs';
import { environment } from 'environments/environment';
import { UsuarioGuardar } from '../../nucleo/modelos/usuarios/usuario-guardar.model';
import { Usuario } from '@modelos/usuarios/usuario.model';
import { Permiso } from '@modelos/usuarios/permiso.model';
import { Rol } from '@modelos/usuarios/rol.model';
import { Pagina } from '@nucleo/util/paginacion/pagina.model';
import { InformacionBasicaUsuario } from '../../nucleo/modelos/usuarios/info-basica-usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioServicio {

    private readonly URL_USUARIOS = `${environment.apiUrl}/usuarios`;

    /**
     * Constructor por defecto. Permite asignar clientes HTTP mockeables para
     * realizacion de pruebas.
     * @param httpClient cliente para realizar peticiones HTTP.
     */
    constructor(private readonly httpClient: HttpClient) { }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param usuario Informacion del usuario a registrar.
     * @returns Observable con el mensaje de exito del servidor.
     */
    registrarUsuario(usuario: UsuarioGuardar): Observable<string> {
        return this.httpClient.post<{ message: string }>(`${this.URL_USUARIOS}/registrar`, usuario)
            .pipe(map(res => {
                return res.message;
            }),
                catchError(this.handleError)
            );
    }

    /**
     * Actualiza la información de un usuario existente.
     * @param id Identificador del usuario a modificar.
     * @param usuario Datos actualizados del usuario.
     * @returns Observable con el mensaje de confirmación.
     */
    modificarUsuario(id: number, usuario: UsuarioGuardar): Observable<string> {
        return this.httpClient.put<{ message: string }>(`${this.URL_USUARIOS}/${id}`, usuario)
            .pipe(
                map(res => res.message),
                catchError(this.handleError)
            );
    }

    /**
     * Obtiene una lista de usuarios paginada con la configuracion dada.
     * @param credenciales Texto para búsqueda.
     * @param pagina Número de página.
     * @param tamano Tamaño de la lista de resultados.
     * @returns Observable con la página de resultados.
     */
    buscarUsuariosPorCredenciales(credenciales: string, pagina: number = 0, tamano: number = 10): Observable<Pagina<InformacionBasicaUsuario>> {

        const params = {
            credenciales,
            page: pagina.toString(),
            size: tamano.toString()
        };

        return this.httpClient.get<Pagina<InformacionBasicaUsuario>>(`${this.URL_USUARIOS}/buscar`, { params })
            .pipe(catchError(this.handleError));
    }

    /**
     * Realiza la eliminación lógica o física de un usuario en el sistema.
     * Al igual que en el registro de roles, se espera una respuesta JSON que contiene un mensaje
     * informativo sobre el resultado de la operación.
     * * @param id - Identificador único del usuario a eliminar.
     * @returns Un `Observable` que emite el mensaje de confirmación (string) extraído de la respuesta del servidor.
     */
    eliminarUsuario(id: number): Observable<string> {
        return this.httpClient.delete<{ message: string }>(`${this.URL_USUARIOS}/${id}`)
            .pipe(
                map(res => res.message),
                catchError(this.handleError)
            );
    }

    /**
     * Recupera la lista de roles asociados a un usuario específico.
     * Realiza una petición GET al servidor para obtener los roles que el usuario
     * tiene asignados actualmente en el sistema.
     * @param id - Identificador único del usuario (Long en el backend).
     * @returns Un `Observable` que emite un arreglo de objetos `Rol[]`.
     */
    obtenerRolesUsuario(id: number): Observable<Rol[]> {
        return this.httpClient.get<Rol[]>(`${this.URL_USUARIOS}/${id}/roles`)
            .pipe(catchError(this.handleError));
    }

    /**
     * Recupera la lista de permisos individuales asociados a un usuario.
     * Este método consulta los permisos específicos que han sido otorgados al usuario,
     * los cuales pueden ser independientes de los roles que posee.
     * @param id - Identificador único del usuario (Long en el backend).
     * @returns Un `Observable` que emite un arreglo de objetos `Permiso[]`.
     */
    obtenerPermisosUsuario(id: number): Observable<Permiso[]> {
        return this.httpClient.get<Permiso[]>(`${this.URL_USUARIOS}/${id}/permisos`)
            .pipe(catchError(this.handleError));
    }

    /**
     * Registra o actualiza la asignación de roles para un usuario específico.
     * Envía una solicitud POST al servidor para vincular una lista de identificadores
     * de roles al usuario indicado por su ID.
     * @param id - Identificador único del usuario al que se le asignarán los roles.
     * @param roles - Arreglo de strings que contiene los identificadores o nombres de los roles.
     * @returns Un `Observable` que emite un mensaje de confirmación del servidor (string) tras la operación exitosa.
     */
    registrarRolesUsuario(id: number, roles: string[]): Observable<string> {
        return this.httpClient.post<{ message: string }>(`${this.URL_USUARIOS}/${id}/roles`, { roles })
            .pipe(map(res => {
                return res.message;
            }),
                catchError(this.handleError)
            )
    }

    /**
     * Obtiene la información de un usuario específico por su ID.
     * Requiere permiso 'USUARIOS_READ_ALL'.
     * @param id Identificador único del usuario.
     * @returns Observable con el UsuarioDTO.
    */
    obtenerUsuarioPorId(id: number): Observable<Usuario> {
        return this.httpClient.get<Usuario>(`${this.URL_USUARIOS}/${id}`)
            .pipe(
                catchError(this.handleError)
            );
    }

    /**
     * Registra o actualiza los permisos de un usuario con el ID dado.
     * Envía una solicitud POST al servidor vincular una lista de identificadores de permisos
     * al usuario.
     * @param id - Identificador único del usuario al que se le asignarán los permisos.
     * @param permisos - Arreglo de string que contiene los identificadores o nombres de los permisos.
     * @returns Un "Observable" que emite un mensaje de confirmación del servidor tras la operación exitosa.
     */
    registrarPermisosUsuario(id: number, permisos: string[]): Observable<string> {
        return this.httpClient.post<{ message: string }>(`${this.URL_USUARIOS}/${id}/permisos`, { permisos })
            .pipe(map(res => {
                return res.message;
            }),
                catchError(this.handleError)
            )
    }

    /**
     * Elimina una lista de roles asignados a un usuario.
     * @param id - Identificador único del usuario.
     * @param roles - Arreglo de identificadores de roles a remover.
     * @returns Un `Observable` con el mensaje de confirmación del servidor.
     */
    removerRolesUsuario(id: number, roles: string[]): Observable<string> {
        return this.httpClient.delete<{ message: string }>(`${this.URL_USUARIOS}/${id}/roles`, {
            body: { roles }
        }).pipe(
            map(res => res.message),
            catchError(this.handleError)
        );
    }

    /**
     * Elimina una lista de permisos asignados a un usuario.
     * @param id - Identificador único del usuario.
     * @param Permisos - Arreglo de identificadores de permisos a remover.
     * @returns Un `Observable` con el mensaje de confirmación del servidor.
     */
    removerPermisosUsuario(id: number, permisos: string[]): Observable<string> {
        return this.httpClient.delete<{ message: string }>(`${this.URL_USUARIOS}/${id}/permisos`, {
            body: { permisos }
        }).pipe(
            map(res => res.message),
            catchError(this.handleError)
        );
    }

    private handleError(error: HttpErrorResponse) {
        // Extraemos el mensaje que viene del backend
        const serverMessage = error.error?.error || 'Error desconocido, revise su conexion, si persiste comuniquese con su provedor';
        const errorDetail = serverMessage;

        return throwError(() => new Error(errorDetail));
    }
}