import { Component, EventEmitter, Output, inject, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';;
import { Router } from '@angular/router';
import { InformacionBasicaUsuario } from '@modelos/usuarios/info-basica-usuario.model';
import { ZardTableComponent } from '@/shared/components/base/table';
import { ZardAlertDialogService } from '@shared/components/base/alert-dialog/alert-dialog.service';
import { ZardSkeletonComponent } from '@/shared/components/base/skeleton';

import { CurrentUserService } from '@/nucleo/servicios/current-user.service';

/**
 * User table component for the admin users page.
 * Displays users in a table with skeleton loading, edit navigation, and delete confirmation.
 * 
 * @example
 * ```html
 * <app-tabla-usuarios
 *   (enEliminacionUsuario)="confirmarEliminacion($event)"
 * />
 * ```
 */
@Component({
  selector: 'app-tabla-usuarios',
  standalone: true,
  templateUrl: './tabla-usuarios.component.html',
  imports: [
    CommonModule,
    ZardTableComponent,
    ZardSkeletonComponent
  ],
})
export class TablaUsuariosComponent {

  /** Signal containing the list of users to display */
  protected usuarios = signal<InformacionBasicaUsuario[]>([]);

  /** Output event emitted when user deletion is confirmed */
  @Output() enEliminacionUsuario = new EventEmitter<InformacionBasicaUsuario>();

  /** Router for navigation to edit page */
  private router: Router = inject(Router);
  private alertDialogService = inject(ZardAlertDialogService);  
  private currentUserService = inject(CurrentUserService);

  /**
   * Comprueba si el ID del usuario de la fila coincide con el ID del usuario en sesión activa.
   */
  esUsuarioActual(userId: number | string): boolean {
    const idSesion = this.currentUserService.getUserId();
    return idSesion > 0 && Number(userId) === Number(idSesion);
  }
  
  /** Array for skeleton loading rows */
  protected skeletonRows = Array.from({ length: 7 });
  /** Loading state signal */
  protected _estaCargando = signal<boolean>(false);
  /** Currently selected user for deletion */
  protected _usuarioSeleccionado?: InformacionBasicaUsuario;
  /** Signal to disable buttons when loading or empty */
  protected _bloquearBotones = signal<boolean>(false);

  /**
   * Sets the users list in the table
   * @param usuarios - Array of users to display
   */
  establecerUsuariosTabla(usuarios: InformacionBasicaUsuario[]) {
    if (!usuarios || usuarios.length === 0) {
      this._bloquearBotones.set(true);
      return;
    }

    this._bloquearBotones.set(false);
    this.usuarios.set(usuarios);
  }

  /**
   * Sets the loading state
   * @param estado - true to show loading skeletons
   */
  estaCargando(estado: boolean) {
    this._estaCargando.set(estado);
  }

  /**
   * Navigates to the edit user page
   * @param usuario - User to edit
   */
  irEditarUsuario(usuario: InformacionBasicaUsuario) {
    this.router.navigate(['/app/usuarios/editar', usuario.id]);
  }

  /**
   * Opens confirmation dialog for user deletion
   * @param usuario - User to delete
   */
  abrirConfirmacionEliminacion(usuario: InformacionBasicaUsuario) {
    this._usuarioSeleccionado = usuario;
    this.alertDialogService.confirm({
      zTitle: 'Confirmar Eliminación',
      zDescription: `¿Estás seguro de que deseas eliminar al usuario '${this._usuarioSeleccionado?.nombre}'`,
      zOkText: 'Continuar',
      zCancelText: 'Cancelar',
      zMaskClosable: true,
      zOnOk: () => {
        this.confirmarEliminacion();
      }
    });
  }

  /**
   * Emits the selected user for deletion
   */
  confirmarEliminacion() {
    if (this._usuarioSeleccionado) {
      this.enEliminacionUsuario.emit(this._usuarioSeleccionado);
    }
  }

  /**
   * Removes the selected user from the table locally
   */
  eliminarUsuarioSeleccionadoTabla() {
    if (this._usuarioSeleccionado) {
      this.usuarios.update(lista =>
        lista.filter(u => u.id !== this._usuarioSeleccionado?.id)
      );
      this._usuarioSeleccionado = undefined;
    }
  }
}
