import { Component, ElementRef, output, signal, ViewChild } from '@angular/core';
import { AppButtonComponent } from '@/shared/components/composed/button/app-button.component';
import { NgClass } from '@angular/common';
import { toastSuccess, toastError } from '@/shared/utils/toast';


@Component({
  selector: 'formulario-imagen',
  templateUrl: './formulario-imagen.component.html',
  imports: [
    AppButtonComponent,
    NgClass
  ]
})
export class FormularioImagenComponente {

  @ViewChild('entradaImagen') private entradaImagen!: ElementRef<HTMLInputElement>;
  @ViewChild('botonGuardarArchivo') private botonGuardarArchivo!: AppButtonComponent;
  @ViewChild('botonDescargarArchivo') private botonDescargarArchivo!: AppButtonComponent;

  download = output();
  fileSave = output();

  protected showUpload = signal<boolean>(false);
  protected _valor = signal<File | null>(null);
  protected _archivoExiste = signal<boolean>(false);
  protected _nombreArchivo = signal<string>('Seleccione un archivo a subir (JPG o PNG)');
  protected _estaArrastrando = signal<boolean>(false);
  protected _descripcionImagen = signal<string>('');
  protected _imagenCargada = signal<string>('');
  protected _extensionesPermitidas: string[] = ['jpg', 'jpeg', 'png'];

  alSubir(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }
    const file = input.files[0];
    this.manejarSeleccionImagen(file);
  }

  alSoltar(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this._estaArrastrando.set(false);
    const file = event.dataTransfer?.files[0];
    if (file) {
      this.manejarSeleccionImagen(file);
    }
  }

  alArrastrarSobre(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this._estaArrastrando.set(true);
  }

  alSalirArrastrar(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this._estaArrastrando.set(false);
  }

  alDescargar(event: Event) {
    event.preventDefault();
    if (!this._valor()) {
      // Delegate the download to the father component if there is no file locally
      this.download.emit();
      return
    }
    this.botonDescargarArchivo.isLoading(true);
    // Download the file locally
    const file = this._valor()!;
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

  alGuardarArchivo(event: Event) {
    event.preventDefault();
    if (!this._valor()) return;
    this.fileSave.emit();
  }

  manejarSeleccionImagen(file: File) {
    try {
      this.validarImagenSeleccionada(file);
      this._valor.set(file); //save the last selected image
      this._archivoExiste.set(true);
      this._nombreArchivo.set(file.name);
      this.establecerVistaPrevia(file);

      toastSuccess('Imagen cargada', `Archivo "${file.name}" seleccionado correctamente`);
    }
    catch (error) {
      if (error instanceof Error) {
        toastError('Error en el archivo', error.message);
      }
    }
  }

  establecerVistaPrevia(file: File) {
    if (!file) return;
    // Create preview URL
    const reader = new FileReader();
    reader.onload = (e) => {
      const result = e.target?.result as string;
      this._imagenCargada.set(result);
    };
    reader.readAsDataURL(file);
  }

  validarImagenSeleccionada(file: File) {
    // Validate file extension
    if (!this.verificarExtensionArchivo(file)) {
      throw new Error('Solo se permiten archivos JPG y PNG');
    }

    // Validate file size
    if (!this.verificarTamanoArchivo(file)) {
      throw new Error('El tamaño máximo permitido es 10MB');
    }
  }

  verificarExtensionArchivo(file: File) {
    return this._extensionesPermitidas.includes(file.name.toLowerCase().split('.').pop() || '');
  }

  verificarTamanoArchivo(file: File) {
    return file.size <= 10 * 1024 * 1024; // 10MB
  }

  obtenerImagenSeleccionada(): File | undefined {
    return this._valor() ?? undefined;
  }

  alLimpiarSeleccion(event: Event) {
    event.preventDefault();
    this.limpiarArchivoSeleccionado();
  }

  limpiarArchivoSeleccionado() {
    this._valor.set(null);
    this.entradaImagen.nativeElement.value = '';
    this._descripcionImagen.set('');
    this._imagenCargada.set('');
    this._archivoExiste.set(false);
    this._nombreArchivo.set('Seleccione un archivo a subir');
  }

}