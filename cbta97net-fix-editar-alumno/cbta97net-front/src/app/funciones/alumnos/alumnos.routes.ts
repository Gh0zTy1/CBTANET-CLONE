import { Routes } from '@angular/router';

export const ALUMNOS_RUTAS: Routes = [
  {
    path: 'registrar-alumno',
    title: 'Registrar Alumno',
    loadComponent: () =>
      import('./registrar-alumno/registrar-alumno.component')
        .then(c => c.RegistrarAlumnoComponente)
  },
  {
    path: 'administracion',
    title: 'Administrar Alumnos',
    loadComponent: () =>
      import('./administrar-alumno/administrar-alumno.component')
        .then(c => c.AdministrarAlumnoComponente),
  },
  {
    path: 'editar/:matricula',
    title: 'Editar Alumno',
    loadComponent: () =>
      import('./editar-alumno/editar-alumno.component')
        .then(c => c.EditarAlumnoComponente),
  }
];
