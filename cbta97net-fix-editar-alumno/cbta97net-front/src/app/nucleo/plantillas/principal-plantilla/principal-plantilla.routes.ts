import { Routes } from '@angular/router';

export const PRINCIPAL_PLANTILLA_RUTAS: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        loadChildren: () =>
          import('../../widgets/sidebar/sidebar.routes')
            .then(r => r.SIDEBAR_PLANTILLA_RUTAS)
      },
    ]
  }
];
