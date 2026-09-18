export interface UsuarioGuardar {
    curp: string;
    nombre: string;
    apellido_paterno: string;
    apellido_materno: string;
    email: string;
    telefono: string;
    contrasena: string;
    activo?: boolean;
}