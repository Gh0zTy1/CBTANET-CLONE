import { Rol } from './rol.model';
import { Permiso } from './permiso.model';

export interface Usuario {
    id?: number;
    curp: string;
    nombre: string;
    apellido_paterno: string;
    apellido_materno: string;
    email: string;
    telefono: string;
    activo: boolean;
    roles?: Rol[];
    permisos?: Permiso[];
}