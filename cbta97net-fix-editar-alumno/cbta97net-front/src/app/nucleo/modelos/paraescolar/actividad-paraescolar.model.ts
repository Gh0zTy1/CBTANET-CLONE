/**
 * Objeto que contiene la información de una actividad paraescolar
 * disponible en el sistema.
 */
export interface ActividadParaescolar {
    id?: number,
    nombre: string,
    descripcion: string
    activo: boolean
}