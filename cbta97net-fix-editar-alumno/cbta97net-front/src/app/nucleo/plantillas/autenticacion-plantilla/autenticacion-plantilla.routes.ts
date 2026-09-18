import { Routes } from '@angular/router';

export const AUTENTICACION_PLANTILLA_RUTAS: Routes = [
  {
    path: '',
    loadChildren: () =>
      import('../../../funciones/autenticacion/autenticacion.routes')
        .then(r => r.AUTENTICACION_RUTAS)
  },
  {
    path: '**',
    redirectTo: 'inicio-sesion'
  }
];
