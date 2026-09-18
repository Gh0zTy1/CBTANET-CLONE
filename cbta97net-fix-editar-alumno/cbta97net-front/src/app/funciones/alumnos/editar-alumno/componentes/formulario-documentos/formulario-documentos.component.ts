import { Component, inject, ViewChild } from "@angular/core";
import { AlumnoServicio } from "@funciones/alumnos/alumno.service";
import { InputUploadComponent } from '@/shared/components/composed/inputs/inline/input-upload/input-upload.component';
import { AlumnoInfoServicio } from "../../editar-alumno-info.service";
import { ZardDividerComponent } from '@/shared/components/base/divider/divider.component';
import { toastError, toastSuccess } from "@/shared/utils/toast";

@Component({
  selector: 'formulario-documentos',
  templateUrl: './formulario-documentos.component.html',
  imports: [
    InputUploadComponent,
    ZardDividerComponent
  ]
})
export class FormularioDocumentosComponente {

  private servicioAlumnos: AlumnoServicio = inject(AlumnoServicio);
  private infoAlumnoServicio: AlumnoInfoServicio = inject(AlumnoInfoServicio);

  @ViewChild('actaNacimiento') private documentoActaNacimientoInput!: InputUploadComponent;
  @ViewChild('certificadoSecundaria') private documentoCertificadoSecundariaInput!: InputUploadComponent;
  @ViewChild('documentoCurp') private documentoCurpInput!: InputUploadComponent;


  /**
   * Establece el nombre de archivo para la entrada del documento del acta de nacimiento.
   * @param nombreDocumento El nombre del documento del acta de nacimiento.
   */
  establecerActaNacimiento(nombreDocumento: string) {
    this.documentoActaNacimientoInput.setFileName(nombreDocumento);
  }

  /**
   * Establece el nombre de archivo para la entrada del documento del certificado de secundaria.
   * @param nombreDocumento El nombre del documento del certificado de secundaria.
   */
  establecerCertificadoSecundaria(nombreDocumento: string) {
    this.documentoCertificadoSecundariaInput.setFileName(nombreDocumento);
  }

  /**
   * Establece el nombre de archivo para la entrada del documento CURP.
   * @param nombreDocumento El nombre del documento CURP.
   */
  establecerCurp(nombreDocumento: string) {
    this.documentoCurpInput.setFileName(nombreDocumento);
  }

  /**
   * Maneja la actualización del documento del acta de nacimiento.
   * Previene el comportamiento predeterminado del evento, recupera la matrícula del alumno y el archivo del acta de nacimiento seleccionado.
   * Si no se selecciona ningún archivo, la función retorna. De lo contrario, establece el botón de guardar en estado de carga,
   * llama al servicio `guardarActaNacimiento` para guardar el documento y muestra un mensaje toast según el resultado.
   * @param event El evento DOM disparado por la acción de actualización.
   */
  actualizarActaNacimiento(event: Event) {
    event.preventDefault();
    event.stopPropagation();

    const matriculaAlumno: string = this.infoAlumnoServicio.obtenerMatricula();
    const actaNacimiento: File | undefined = this.documentoActaNacimientoInput.getSelectedFile();
    if (!actaNacimiento) {
      return
    }
    
    this.documentoActaNacimientoInput.isSaveButtonLoading(true);

    this.servicioAlumnos.guardarActaNacimiento(matriculaAlumno, actaNacimiento).subscribe({
      next: (data) => {
        this.documentoActaNacimientoInput.isFileSaved(true);
        this.documentoActaNacimientoInput.isSaveButtonLoading(false);
        toastSuccess('Éxito', 'Acta de nacimiento registrada correctamente');
      },
      error: (error: any) => {
        toastError('Error al registrar acta de nacimiento', error.message);
        this.documentoActaNacimientoInput.isSaveButtonLoading(false);
      }
    });
  }

  /**
   * Maneja la actualización del documento del certificado de secundaria.
   * Previene el comportamiento predeterminado del evento, recupera la matrícula del alumno y el archivo del certificado de secundaria seleccionado.
   * Si no se selecciona ningún archivo, la función retorna. De lo contrario, establece el botón de guardar en estado de carga,
   * llama al servicio `guardarCertificadoSecundaria` para guardar el documento y muestra un mensaje toast basado en el resultado.
   * @param event El evento DOM disparado por la acción de actualización.
   */
  actualizarCertificadoSecundaria(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    const matriculaAlumno: string = this.infoAlumnoServicio.obtenerMatricula();
    const certificadoSecundaria: File | undefined = this.documentoCertificadoSecundariaInput.getSelectedFile();
    if (!certificadoSecundaria) {
      return
    }
    this.documentoCertificadoSecundariaInput.isSaveButtonLoading(true);

    this.servicioAlumnos.guardarCertificadoSecundaria(matriculaAlumno, certificadoSecundaria).subscribe({
      next: (data) => {
        this.documentoCertificadoSecundariaInput.isFileSaved(true);
        this.documentoCertificadoSecundariaInput.isSaveButtonLoading(false);
        toastSuccess('Éxito', 'Certificado de secundaria registrado correctamente');
      },
      error: (error: any) => {
        toastError('Error al registrar certificado de secundaria', error.message);
        this.documentoCertificadoSecundariaInput.isSaveButtonLoading(false);
      }
    });
  }

  /**
   * Maneja la actualización del documento CURP.
   * Previene el comportamiento predeterminado del evento, recupera la matrícula del alumno y el archivo CURP seleccionado.
   * Si no se selecciona ningún archivo, la función retorna. De lo contrario, establece el botón de guardar en estado de carga,
   * llama al servicio `guardarDocumentoCURP` para guardar el documento y muestra un mensaje toast basado en el resultado.
   * @param event El evento DOM disparado por la acción de actualización.
   */
  actualizarCurp(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    this.documentoCurpInput.isFileSaved(true);
    const matriculaAlumno: string = this.infoAlumnoServicio.obtenerMatricula();
    const curp: File | undefined = this.documentoCurpInput.getSelectedFile();
    if (!curp) {
      return
    }
    this.documentoCurpInput.isSaveButtonLoading(true);

    this.servicioAlumnos.guardarDocumentoCURP(matriculaAlumno, curp).subscribe({
      next: (data) => {
        this.documentoCurpInput.isFileSaved(true);
        this.documentoCurpInput.isSaveButtonLoading(false);
        toastSuccess('Éxito', 'CURP registrada correctamente');
      },
      error: (error: any) => {
        toastError('Error al registrar CURP', error.message);
        this.documentoCurpInput.isSaveButtonLoading(false);
      }
    });
  }

  /**
   * Maneja la descarga del acta de nacimiento. Previene el comportamiento predeterminado del evento,
   * obtiene la matrícula del alumno y, si está disponible, llama al servicio `obtenerActaNacimiento`.
   * Si la matrícula no está disponible, muestra un mensaje de error. Si la descarga es exitosa,
   * convierte el blob a un archivo, lo establece en el input del acta de nacimiento y lo descarga.
   * @param event El evento DOM disparado por la acción de descarga.
   */
  descargarActaNacimiento() {
    console.log('se esta descargando de la nube')
    
    const matriculaAlumno: string = this.infoAlumnoServicio.obtenerMatricula();
    if (!matriculaAlumno) {
      toastError('Error', 'No se pudo obtener la matrícula del alumno'); 
      return;
    }
    this.documentoActaNacimientoInput.isDownloadButtonLoading(true);

    this.servicioAlumnos.obtenerActaNacimiento(matriculaAlumno).subscribe({
      next: (blob) => {
        const archivo = this.convertirAArchivo(blob, 'actaNacimiento');
        this.documentoActaNacimientoInput.setFile(archivo);
        this.documentoActaNacimientoInput.downloadFile(archivo);
        this.documentoActaNacimientoInput.isFileSaved(true);
        this.documentoActaNacimientoInput.isDownloadButtonLoading(false);
      },
      error: (error: any) => {
        toastError('Error al descargar acta de nacimiento', error.message);
        this.documentoActaNacimientoInput.isFileSaved(false);
        this.documentoActaNacimientoInput.isDownloadButtonLoading(false);
      }
    });
  }

  /**
   * Maneja la descarga del documento CURP. Previene el comportamiento predeterminado del evento,
   * obtiene la matrícula del alumno y, si está disponible, llama al servicio `obtenerDocumentoCURP`.
   * Si la matrícula no está disponible, muestra un mensaje de error. Si la descarga es exitosa,
   * convierte el blob a un archivo, lo establece en el input del CURP y lo descarga.
   * @param event El evento DOM disparado por la acción de descarga.
   */
  descargarDocumentoCURP(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    const matricula: string = this.infoAlumnoServicio.obtenerMatricula();
    if (!matricula) {
      toastError('Error', 'No se pudo obtener la matrícula del alumno');
      return;
    }
    this.documentoCurpInput.isDownloadButtonLoading(true);

    this.servicioAlumnos.obtenerDocumentoCURP(matricula).subscribe({
      next: (blob) => {
        const archivo = this.convertirAArchivo(blob, 'actaNacimiento');
        this.documentoCurpInput.setFile(archivo);
        this.documentoCurpInput.downloadFile(archivo);
        this.documentoCurpInput.isFileSaved(true);
        this.documentoCurpInput.isDownloadButtonLoading(false);
      },
      error: (error: any) => {
        toastError('Error al descargar CURP', error.message);
        this.documentoCurpInput.isFileSaved(false);
        this.documentoCurpInput.isDownloadButtonLoading(false);
      }
    });
  }

  /**
   * Maneja la descarga del certificado de secundaria. Previene el comportamiento predeterminado del evento,
   * obtiene la matrícula del alumno y, si está disponible, llama al servicio `obtenerCertificadoSecundaria`.
   * Si la matrícula no está disponible, muestra un mensaje de error. Si la descarga es exitosa,
   * convierte el blob a un archivo, lo establece en el input del certificado de secundaria y lo descarga.
   * @param event El evento DOM disparado por la acción de descarga.
   */
  descargarCertificadoSecundaria(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    const matricula: string = this.infoAlumnoServicio.obtenerMatricula();
    if (!matricula) {
      toastError('Error', 'No se pudo obtener la matrícula del alumno');
      return;
    }
    this.documentoCertificadoSecundariaInput.isDownloadButtonLoading(true);

    this.servicioAlumnos.obtenerCertificadoSecundaria(matricula).subscribe({
      next: (blob) => {
        const archivo = this.convertirAArchivo(blob, 'actaNacimiento');
        this.documentoCertificadoSecundariaInput.setFile(archivo);
        this.documentoCertificadoSecundariaInput.downloadFile(archivo);
        this.documentoCertificadoSecundariaInput.isFileSaved(true);
        this.documentoCertificadoSecundariaInput.isDownloadButtonLoading(false);
      },
      error: (error: any) => {
        toastError('Error al descargar certificado de secundaria', error.message);
        this.documentoCertificadoSecundariaInput.isFileSaved(false);
        this.documentoCertificadoSecundariaInput.isDownloadButtonLoading(false);
      }
    });
  }

  /**
   * Convierte un objeto de tipo blob a archivo.
   * @param blob Archivo de tipo blob a convertir.
   * @param nombreArchivo Nombre del archivo a convertir.
   * @returns Archivo de tipo File.
   */
  public convertirAArchivo = (blob: Blob, nombreArchivo: string): File => {
    // If it's already a File, return as-is
    if (blob instanceof File) return blob;
    const mime = (blob as any).type || 'application/octet-stream';
    try {
      return new File([blob], nombreArchivo, { type: mime });
    } catch {
      // Fallback: attempt to cast to File-like object
      const f = blob as unknown as File;
      (f as any).name = nombreArchivo;
      return f;
    }
  }

}