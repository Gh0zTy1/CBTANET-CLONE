export interface Alumno {
  id?: number;
  matricula: string;
  curp?: string;
  nombre?: string;
  apellido_paterno?: string;
  apellido_materno?: string;
  fecha_nacimiento?: Date;
  nss?: number;
  poliza_seguro?: number;
  condicion_especial_desc?: string;
  doc_acta_nacimiento?: string;
  doc_certificado_secundaria?: string;
  doc_curp?: string;
  doc_foto_escolar?: string;
  tutor_legal?: Tutor;
  direccion?: Direccion
}

export interface Tutor {
  id?: number;
  parentesco?: string;
  nombre?: string;
  apellido_paterno?: string;
  apellido_materno?: string;
  fecha_nacimiento?: Date;
  telefono?: string;
}

export interface Direccion {
  id?: number;
  calle?: string
  colonia?: string
  numero_exterior?: number
  codigo_postal?: number
  localidad?: string
} 
