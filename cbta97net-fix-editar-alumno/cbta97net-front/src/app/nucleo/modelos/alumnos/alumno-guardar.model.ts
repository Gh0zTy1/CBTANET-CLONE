export interface AlumnoGuardar {
  matricula: string;
  curp: string;
  nombre: string;
  apellido_paterno: string;
  apellido_materno: string;
  fecha_nacimiento: Date;
  nss: number | null;
  poliza_seguro: number | null;
  condicion_especial_desc: string | null;
  tutor_legal: Tutor | null;
  direccion: Direccion | null;
}

export interface Tutor {
  id?: number;
  parentesco: string | null;
  nombre: string | null;
  apellido_paterno: string | null;
  apellido_materno: string | null;
  fecha_nacimiento?: Date | null;
  telefono: string | null;
}

export interface Direccion {
  id?: number | null;
  calle: string | null
  colonia: string | null
  numero_exterior: number | null
  codigo_postal: number | null
  localidad: string | null
} 
