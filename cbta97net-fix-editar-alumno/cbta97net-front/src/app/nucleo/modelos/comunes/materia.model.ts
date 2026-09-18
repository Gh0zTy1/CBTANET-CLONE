
export interface Materia {
  id?: number;
  nombre: string;
  horas_por_semana: number;
  semestre: number;
  carrera_tecnica_id?: number,
  area_propedeutica_id?: number,
  carrera_tecnica_nombre?: string;
  area_propedeutica_nombre?: string;
  //unidades: Unidad[];
}