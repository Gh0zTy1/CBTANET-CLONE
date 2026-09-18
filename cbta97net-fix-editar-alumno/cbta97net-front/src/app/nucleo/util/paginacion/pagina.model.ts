
/**
 * Contiene la estructura estándar definida para las respuestas paginadas
 * de Springboot.
 * @param T Tipo de objeto que vendrá en "content: T[]".
 */
export interface Pagina<T> {
  content: T[];

  number: number;
  size: number;
  totalElements: number;
  totalPages: number;

  first: boolean;
  last: boolean;
  empty: boolean;
}