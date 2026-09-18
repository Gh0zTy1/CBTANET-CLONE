import { ClaseParaescolar } from "./clase-paraescolar.model";

/**
 * Objeto utilizado para mapear las respuestas del servidor en objetos
 * utilizables de grupo paraescolar para la Interfaz Gráfica.
 */
export interface GrupoParaescolar {
  id?: number;
  nota?: string;
  maximo_espacios?: number;
  ciclo_escolar_id: number;
  actividad_paraescolar_id: number;
  docente_id: number;
  clases?: ClaseParaescolar[];
  alumnos_inscritos?: string[];
}