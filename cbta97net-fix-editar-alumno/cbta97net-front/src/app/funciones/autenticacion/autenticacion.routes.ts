import { Routes } from '@angular/router';
import { InicioSesionComponente } from './inicio-sesion/inicio-sesion.component';

export const AUTENTICACION_RUTAS: Routes = [
  {
    path: 'inicio-sesion',
    title: 'Iniciar Sesión',
    loadComponent: () =>
      import('./inicio-sesion/inicio-sesion.component')
        .then(c => c.InicioSesionComponente)
  }
];
