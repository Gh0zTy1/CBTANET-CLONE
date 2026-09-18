import { Routes } from '@angular/router';

export const SIDEBAR_PLANTILLA_RUTAS: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'inicio'
  },
  {
    path: 'inicio',
    loadChildren: () => import('../dashboard/dashboard.routes')
    .then(r => r.DASHBOARD_RUTAS)
  },
  {
    path: 'alumnos',
    loadChildren: () => import('../../../funciones/alumnos/alumnos.routes')
    .then(r => r.ALUMNOS_RUTAS)
  },
  {
    path: 'usuarios',
    loadChildren: () => import('../../../funciones/usuarios/usuarios.routes')
    .then(r => r.USUARIOS_ROUTES)
  },
];
