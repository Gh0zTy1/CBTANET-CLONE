import { NgClass } from '@angular/common';
import { Component, ElementRef, EventEmitter, inject, input, Output, signal, ViewChild } from '@angular/core';
import { AppButtonComponent } from '@/shared/components/composed/button/app-button.component';
import { AlumnoServicio } from '@funciones/alumnos/alumno.service';
import { AlumnoInfoServicio } from '../../editar-alumno-info.service';
import { ZardTooltipImports } from '@/shared/components/base/tooltip';
import { toastSuccess, toastError } from "@/shared/utils/toast";

@Component({
  selector: 'formulario-imagen',
  templateUrl: './formulario-imagen.component.html',
  imports: [
    NgClass,
    AppButtonComponent,
    ZardTooltipImports
  ]
})
/**
 * Componente para la gestión de imágenes de alumnos, permitiendo subir, previsualizar, guardar y descargar fotos escolares.
 * Realiza validaciones de extensión y tamaño de archivo.
 */
export class FormularioImagenComponente {

  private servicioAlumnos: AlumnoServicio = inject(AlumnoServicio);
  private infoAlumnoServicio: AlumnoInfoServicio = inject(AlumnoInfoServicio);

  @ViewChild('entradaImagen') private entradaImagen!: ElementRef<HTMLInputElement>;
  @ViewChild('botonGuardarArchivo') private botonGuardarArchivo!: AppButtonComponent;
  @ViewChild('botonDescargarArchivo') private botonDescargarArchivo!: AppButtonComponent;

  @Output() download = new EventEmitter<void>();
  @Output() fileSave = new EventEmitter<void>();

  showUpload = input<boolean>(true);

  protected _valor = signal<File | null>(null);
  protected _ultimoImagenAmacenada = signal<File | null>(null);
  protected _estaArchivoGuardado = signal<boolean>(false);
  protected _archivoExiste = signal<boolean>(false);
  protected _nombreArchivo = signal<string>('Seleccione un archivo a subir');
  protected _estaArrastrando = signal<boolean>(false);
  protected _imagenCargada = signal<string>('');
  protected _extensionesPermitidas: string[] = ['jpg', 'jpeg', 'png'];
  // (no additional client-side cache)
  
  /**
   * Maneja el evento de selección de archivo cuando se sube una imagen a través del input.
   * Valida el archivo seleccionado y establece la vista previa.
   * @param event El evento de cambio del input de archivo.
   */
  protected alSubir(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }
    const file = input.files[0];
    this.manejarSeleccionImagen(file);
  }

  /**
   * Maneja el evento de soltar un archivo en el área de carga (drag and drop).
   * Previene el comportamiento por defecto, detiene la propagación del evento, desactiva el estado de arrastre y procesa el archivo soltado.
   * @param event El evento de arrastre y soltado.
   */
  protected alSoltar(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this._estaArrastrando.set(false);
    const file = event.dataTransfer?.files[0];
    if (file) {
      this.manejarSeleccionImagen(file);
    }
  }

  /**
   * Maneja el evento de arrastrar un archivo sobre el área de carga (drag and drop).
   * Previene el comportamiento por defecto, detiene la propagación del evento y activa el estado de arrastre.
   * @param event El evento de arrastre.
   */
  protected alArrastrarSobre(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this._estaArrastrando.set(true);
  }

  /**
   * 
   * Maneja el evento de salir del área de arrastre (drag and drop).
   * Previene el comportamiento por defecto, detiene la propagación del evento y desactiva el estado de arrastre.
   * @param event El evento de arrastre.
   */
  protected alSalirArrastrar(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this._estaArrastrando.set(false);
  }

  /**
   * Maneja el evento de descarga del archivo. 
   * Previene el comportamiento por defecto del evento. 
   * Llama a manejarDescargaImagen() para descargar la imagen.
   * @param evento El evento del clic.
   */
  protected manejarDescargaImagen(evento: Event) {
    evento.preventDefault();
    evento.stopPropagation();
    // No client-side cache logic; rely on server/previous flows
    if (!this._estaArchivoGuardado()) {
      if (this._ultimoImagenAmacenada()) {
        this.descargaImagen(this._ultimoImagenAmacenada());
        return
      }
      // there isn't a value and is not saved, request the download
      this.descarImagenServidor();
      return
    }
    // Download the current file
    this.botonDescargarArchivo.isLoading(true);
    this.descargaImagen(this._valor());
    this.botonDescargarArchivo.isLoading(false);
  }

  /**
   * Maneja la lógica para descargar la imagen actualmente seleccionada.
   * Crea un URL de objeto para el archivo, simula un clic en un enlace de descarga y luego revoca el URL para liberar memoria.
   */
  private descargaImagen(file: File | null) {
    if(!file) return
    this.botonDescargarArchivo.isLoading(true);
    const url = URL.createObjectURL(file);

    const link = document.createElement('a');
    link.href = url;
    link.download = file.name;
    link.style.display = 'none';

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    // Clean URL to free memory
    URL.revokeObjectURL(url);
    this.botonDescargarArchivo.isLoading(false);
  }

  /**
   * Maneja la selección de una imagen, realizando validaciones y estableciendo la vista previa.
   * Si la validación falla, muestra un mensaje de error.
   * @param file El archivo de imagen seleccionado.
   */
  private manejarSeleccionImagen(file: File) {
    try {
      this.validarImagenSeleccionada(file);
      this.establecerValorImagen(file);
    }
    catch (error) {
      if (error instanceof Error) {
        toastError('Error en el archivo', error.message);
      }
    }
  }

  /**
   * Establece el archivo de imagen actual y actualiza la vista previa.
   * @param file El archivo de imagen a establecer.
   */
  establecerValorImagen(file: File) {
    if (!file) return;
    this._valor.set(file);
    this._nombreArchivo.set(file.name);
    this._archivoExiste.set(true);
    this._estaArchivoGuardado.set(false);
    this.establecerVistaPrevia(file);
  }

  /**
   * Establece el nombre del archivo de imagen mostrado en el componente.
   * @param nombre El nombre del archivo a mostrar.
   */
  establecerNombreImagen(nombre: string) {
    if (!nombre) {
      this._nombreArchivo.set('Seleccione un archivo a subir')
      this._estaArchivoGuardado.set(false);
      this._archivoExiste.set(false);
      return;
    };
    this._nombreArchivo.set(nombre);
    this._estaArchivoGuardado.set(false);
    this._archivoExiste.set(true);
  }

  /**
   * Maneja el evento de guardar el archivo. 
   * Previene el comportamiento por defecto del evento. 
   * Emite el evento 'fileSave' si hay un archivo seleccionado.
   * @param event El evento del clic.
   */
  protected alActualizarArchivo(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    this.actualizarImagen();
  }

  /**
   * Actualiza la imagen escolar de un alumno en el servidor, utilizando la matrícula del alumno y la imagen seleccionada.
   * Muestra mensajes flotantes de éxito o error dependiendo del resultado de la operación.
   * @param event El evento que desencadena la actualización, típicamente un evento de clic.
   */
  protected actualizarImagen() {
    if (!this._valor()) return;

    const matriculaAlumno: string = this.infoAlumnoServicio.obtenerMatricula();
    const imagenAlumno: File | undefined = this.obtenerImagenSeleccionada();

    if (!matriculaAlumno) {
      toastError('Error', 'No se proporcionó la matrícula del alumno');
      return;
    }

    if (!imagenAlumno) {
      return;
    }

    this.botonGuardarArchivo.isLoading(true);
    this.servicioAlumnos.guardarFotoEscolar(matriculaAlumno, imagenAlumno).subscribe({
      next: (data) => {
        this._ultimoImagenAmacenada.set(this._valor());
        this._estaArchivoGuardado.set(true);
        this.botonGuardarArchivo.isLoading(false);
        toastSuccess('Éxito', 'Imagen registrada correctamente');
      },
      error: (error: any) => {
        this.botonGuardarArchivo.isLoading(false);
        toastError('Error al registrar imagen', error.message);
      }
    });
  }

  /**
   * Funcion auxiliar sincrona encargada de establecer la foto del alumno seleccionado.
   */
  async descarImagenServidor(matriculaAlumno?: string): Promise<void> {
    console.info('se establecio la foto de perfil');
    
    const matricula = matriculaAlumno || this.infoAlumnoServicio.obtenerMatricula();

    if (!matricula) throw new Error('No se proporcionó la matrícula del alumno');
  
    this.servicioAlumnos.obtenerFotoEscolar(matricula).subscribe({
      next: (data) => {
        const imagen = this.convertirAArchivo(data, 'imagen_alumno.jpg');
        this.establecerValorImagen(imagen);
      },
      error: (error: any) => {
        toastError("Error al descargar la imagen", error.message)
      }
    });
  }

  /**
   * Establece la vista previa de la imagen seleccionada. 
   * Utiliza FileReader para leer el archivo como una URL de datos y actualizar la señal `_imagenCargada`.
   * @param file El archivo de imagen para el cual se generará la vista previa.
   */
  private establecerVistaPrevia(file: File) {
    if (!file) return;
    // Create preview URL
    const reader = new FileReader();
    reader.onload = (e) => {
      const result = e.target?.result as string;
      this._imagenCargada.set(result);
    };
    reader.readAsDataURL(file);
  }

  /**
   * Obtiene la imagen actualmente seleccionada.
   * @returns El archivo de imagen seleccionado o `undefined` si no hay ninguno.
   */
  obtenerImagenSeleccionada(): File | undefined {
    return this._valor() ?? undefined;
  }

  /**
   * Valida la imagen seleccionada comprobando su extensión y tamaño.
   * Lanza un error si la extensión no es permitida (solo JPG y PNG) o si el tamaño excede los 10MB.
   * @param file El archivo de imagen a validar.
   * @throws Error Si la extensión del archivo no es JPG o PNG, o si el tamaño del archivo excede los 10MB.
   */
  private validarImagenSeleccionada(file: File) {
    // Validate file extension
    if (!this.verificarExtensionArchivo(file)) {
      throw new Error('Solo se permiten archivos JPG y PNG');
    }

    // Validate file size
    if (!this.verificarTamanoArchivo(file)) {
      throw new Error('El tamaño máximo permitido es 10MB');
    }
  }

  /**
   * Verifica si la extensión del archivo dado está permitida.
   * @param file El archivo a verificar.
   * @returns `true` si la extensión del archivo está permitida, `false` en caso contrario.
   */
  private verificarExtensionArchivo(file: File) {
    return this._extensionesPermitidas.includes(file.name.toLowerCase().split('.').pop() || '');
  }

  /**
   * Verifica si el tamaño del archivo dado es menor o igual a 10MB.
   * @param file El archivo a verificar.
   * @returns `true` si el tamaño del archivo es menor o igual a 10MB, `false` en caso contrario.
   */
  private verificarTamanoArchivo(file: File) {
    return file.size <= 10 * 1024 * 1024; // 10MB
  }

  /**
   * Maneja el evento de limpiar la selección de archivo. 
   * Previene el comportamiento por defecto del evento. 
   * Llama a limpiarArchivoSeleccionado() para restablecer el estado del componente.
   * @param event El evento del clic.
   */
  protected alLimpiarSeleccion(event: Event) {
    event.preventDefault();
    this.limpiarArchivoSeleccionado();
  }

  /**
   * Restablece el estado del componente, limpiando el archivo seleccionado, la vista previa y el nombre del archivo.
   */
  limpiarArchivoSeleccionado() {
    this._valor.set(null);
    this.entradaImagen.nativeElement.value = '';
    this._imagenCargada.set('');
    this._archivoExiste.set(false);
    this._estaArchivoGuardado.set(false);
    this._nombreArchivo.set('Seleccione un archivo a subir');
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
