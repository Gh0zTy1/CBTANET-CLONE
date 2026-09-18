import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from "../../servicios/auth.service";
import { SidebarHeaderComponent } from "./components/nav-header/nav-header.component";
import { SidebarBodyComponent } from './components/nav-body/nav-body.component';
import { SidebarFooterComponent } from "./components/nav-footer/nav-footer.component";

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  imports: [
    SidebarHeaderComponent,
    SidebarBodyComponent,
    SidebarFooterComponent,
  ]
})
export class SidebarPlantillaComponente {

  private authService: AuthService = inject(AuthService);

  constructor(private router: Router) {

  }

  /**
   * Cerrar sesion
   */
  cerrarSesion() {

    this.authService.cerrarSesion().subscribe({
      next: (res) => {
        console.info('Respuesta del servidor:', res.message);
        console.info("Sesion cerrada correctamente");

        this.router.navigate(['/autenticacion/inicio-sesion']);

      },
      error: (err) => {
        console.error('Error al cerrar sesión:', err);
        // TODO: mostrar algun mensaje de error...
      }
    });
  }


}