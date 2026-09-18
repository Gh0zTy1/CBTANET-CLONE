import { Routes } from '@angular/router';
import { AutenticacionGuardian } from './nucleo/guardianes/autenticacion.guard';
import { AutenticacionPlantillaComponente } from './nucleo/plantillas/autenticacion-plantilla/autenticacion-plantilla.component'
import { PrincipalPlantillaComponente } from './nucleo/plantillas/principal-plantilla/principal-plantilla.component'

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/autenticacion/inicio-sesion'
  },
  {
    path: 'autenticacion',
    component: AutenticacionPlantillaComponente,
    loadChildren: () =>
      import('./nucleo/plantillas/autenticacion-plantilla/autenticacion-plantilla.routes')
        .then(r => r.AUTENTICACION_PLANTILLA_RUTAS)
  },
  {
    path: 'app',
    component: PrincipalPlantillaComponente,
    canActivate: [AutenticacionGuardian],
    loadChildren: () =>
      import('./nucleo/plantillas/principal-plantilla/principal-plantilla.routes')
        .then(r => r.PRINCIPAL_PLANTILLA_RUTAS)
  },
  {
    path: '**',
    redirectTo: 'inicio-sesion'
  }
];
