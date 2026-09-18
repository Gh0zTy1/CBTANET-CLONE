import { Routes } from '@angular/router';

export const DASHBOARD_RUTAS: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard'
  },
  {
    path: 'dashboard',
    title: 'Dasboard',
    loadComponent: () =>
      import('./dashboard.component')
        .then(c => c.DashboardComponent)
  }
];
