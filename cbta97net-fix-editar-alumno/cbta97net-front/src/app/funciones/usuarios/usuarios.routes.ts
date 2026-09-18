import { Routes } from '@angular/router';

export const USUARIOS_ROUTES: Routes = [
  {
    path: 'registrar-usuario',
    title: 'Registrar Usuario',
    loadComponent: () =>
      import('./registrar-usuario/registrar-usuario.component')
        .then(c => c.RegistrarUsuarioComponente)
  },
  {
    path: 'administrar-usuarios',
    title: 'Administrar Usuarios',
    loadComponent: () => 
      import('./administrar-usuarios/administrar-usuarios.component')
        .then(c => c.AdministrarUsuarioComponent)
  },
  {
    path: 'editar/:id',
    title: 'Editar Alumno',
    loadComponent: () =>
      import('./editar-usuario/editar-usuario.component')
        .then(c => c.EditarUsuarioComponente),
  }
];
