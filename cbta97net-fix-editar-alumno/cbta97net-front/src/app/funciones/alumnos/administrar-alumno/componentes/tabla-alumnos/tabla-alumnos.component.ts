import { Component, EventEmitter, Output, inject, signal, ViewChild, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { InformacionBasicaAlumno } from '@modelos/alumnos/informacion-basica-alumno.model';
import { ZardTableComponent } from '@/shared/components/base/table';
import { ZardSkeletonComponent } from '@/shared/components/base/skeleton';
import { ZardAlertDialogService } from '@shared/components/base/alert-dialog/alert-dialog.service';

@Component({
  selector: 'app-tabla-busqueda-alumnos',
  standalone: true,
  templateUrl: './tabla-alumnos.component.html',
  imports: [
    CommonModule,
    ZardTableComponent,
    ZardSkeletonComponent
  ],
})
export class TablaAlumnosComponent {

  private alertDialogService = inject(ZardAlertDialogService);  
  private router: Router = inject(Router);

  protected enEliminacionAlumno = output<InformacionBasicaAlumno>({});
  
  protected _alumnos = signal<InformacionBasicaAlumno[]>([]);
  protected _skeletonRows = Array.from({ length: 7 });
  protected _estaCargando = signal<boolean>(false);
  protected _alumnoSeleccionado?: InformacionBasicaAlumno;
  protected _bloquearBotones = signal<boolean>(false);

  establecerAlumnosTabla(alumnos: InformacionBasicaAlumno[]) {
    if (!alumnos || alumnos.length === 0) {
      this._bloquearBotones.set(true);
      return;
    }

    this._bloquearBotones.set(false);
    this._alumnos.set(alumnos);
  }

  estaCargando(estado: boolean) {
    this._estaCargando.set(estado);
  }

  irEditarAlumno(alumno: InformacionBasicaAlumno) {
    this.router.navigate(['/app/alumnos/editar', alumno.matricula]);
  }

  abrirConfirmacionEliminacion(alumno: InformacionBasicaAlumno) {
    this._alumnoSeleccionado = alumno;
    this.alertDialogService.confirm({
      zTitle: 'Confirmar Eliminación',
      zDescription: `¿Estás seguro de que deseas eliminar al alumno '${this._alumnoSeleccionado?.nombre}'`,
      zOkText: 'Continuar',
      zCancelText: 'Cancelar',
      zMaskClosable: true,
      zOnOk: () => {
        this.confirmarEliminacion();
      }
    });
  }

  confirmarEliminacion() {
    if (this._alumnoSeleccionado) {
      this.enEliminacionAlumno.emit(this._alumnoSeleccionado);
    }
  }

  eliminarAlumnoSeleccionadoTabla() {
    if (this._alumnoSeleccionado) {
      this._alumnos.update(lista =>
        lista.filter(a => a.matricula !== this._alumnoSeleccionado?.matricula)
      );
      this._alumnoSeleccionado = undefined;
    }
  }
}
