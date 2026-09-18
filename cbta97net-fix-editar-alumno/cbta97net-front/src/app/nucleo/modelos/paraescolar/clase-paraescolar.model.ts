import { Horario } from "./horario.model";

/**
 * Objeto utilizado mapear las respuestas del servidor. Además de 
 * ser útil para registrar clases junto con un grupo paraescolar.
 */
export interface ClaseParaescolar {
  actividad_paraescolar_id: number;
  actividad_paraescolar?: any;
  docente_id: number;
  grupo_paraescolar_id: number;
  aula_id: number;
  aula?: any;
  ciclo_escolar_id?: number;
  ciclo_escolar?: any;
  docente_nombre?: string;
  docente_apellido_paterno?: string;
  docente_apellido_materno?: string;
  horario: Horario;
}