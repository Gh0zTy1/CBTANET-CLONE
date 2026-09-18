import { Component, inject, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppButtonComponent } from '@/shared/components/composed/button/app-button.component';
import { AlumnoServicio } from '@funciones/alumnos/alumno.service';
import { DragDropInputComponent } from '@/shared/components/composed/inputs/inline/drag-drop-input/drag-drop-input.component';
import { ZardDialogService } from '@/shared/components/base/dialog';
import { ZardDialogRef } from '@/shared/components/base/dialog';
import { AlertComposedComponent } from '@/shared/components/base/alert/alert.component';
import { toastSuccess } from '@/shared/utils/toast';

@Component({
  standalone: true,
  selector: 'modal-massive-insertion',
  template: ''
})
export class ModalInsercionMasivaComponent {

  private dialogService = inject(ZardDialogService);

  /**
   * Function to open the modal.
   */
  abrirModal() {
    this.dialogService.create({
      zTitle: 'Inserción Masiva de Alumnos',
      zDescription: `Cargue el archivo Excel de SISEEMS para registrar alumnos masivamente.`,
      zContent: ModalInsercionMasivaTemplate,
      zHideFooter: true,
      zMaskClosable: false,
    });
  }
}


@Component({
  selector: 'modal-massive-insertion',
  templateUrl: './modal-insercion-masiva.component.html',
  imports: [
    CommonModule,
    AppButtonComponent,
    DragDropInputComponent,
    AlertComposedComponent
  ]
})
export class ModalInsercionMasivaTemplate {

  @ViewChild('uploadZone') protected dragDropInput!: DragDropInputComponent;
  @ViewChild('saveButton') protected botonGuardar!: AppButtonComponent;

  private dialog = inject(ZardDialogRef);
  private readonly alumnoServicio = inject(AlumnoServicio);

  protected esError = signal<boolean>(false);
  protected tituloError = signal<string>('');
  protected mensajeError = signal<string>('');

  /**
   * Orquestador principal de la subida del archivo csv a el servidor
   * @returns void.
   */
  importarArchivo(): void { 
    const file = this.dragDropInput.getFile();
    if (!file) return;

    this.botonGuardar.isLoading(true);

    this.alumnoServicio.importarAlumnosSiseems(file).subscribe({
      next: data => {
        this.botonGuardar.isLoading(false);
        this.dragDropInput.cleanSelectedFile();
        this.dialog.close(); // close the dialog
        toastSuccess('Proceso Completado', data.message || 'Los alumnos han sido importados correctamente.');
      },
      error: (err: any) => {
        this.botonGuardar.isLoading(false);
      }
    });
  }

  /**
   * Cerrar la modal de manera manual.
   * @param event nose
   */
  cerrarModal(event: Event): void {
    event.preventDefault();
    event.stopPropagation(); 
    this.dialog.close(); // close the dialog
  }

  /**
   * Maneja el orquestado de la insercion de alumnos, procesa validaciones y delega a
   * la funcion de importacion.
   */
  protected manejarImportadoArchivo(event: Event): void {
    event.preventDefault();
    event.stopPropagation();

    //previus validations

    this.importarArchivo()
  }

  /**
   * Manejo de los errores del componente de anclado de archivos.
   * @param mensaje Mensaje de error.
   */
  protected manejarErrorInputArchivos(mensaje: string): void {
    this.esError.set(true)
    this.tituloError.set('Error al anclar el archivo')
    this.mensajeError.set(mensaje)
  }

  /**
   * Manejo de estado en caso de que un archivo haya sido anclado de manera exitosa en 
   * el componente de entrada de archivos.
   */
  protected manejarAncladoExitoso(): void{
    this.esError.set(false);
    this.tituloError.set('')
    this.mensajeError.set('')
  }

}


