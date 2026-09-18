import { ClaseParaescolar } from "./clase-paraescolar.model";

/**
 * Objeto utilizado únicamente para registrar nuevos grupos 
 * paraescolares en el sistema.
 */
export interface CrearGrupoParaescolar {
  id?: number;
  nota?: string;
  maximo_espacios: number;
  actividad_paraescolar_id: number;
  docente_id: number;
  clases: ClaseParaescolar[];
}