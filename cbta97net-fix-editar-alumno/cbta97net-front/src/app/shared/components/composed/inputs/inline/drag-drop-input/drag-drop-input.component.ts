import { Component, EventEmitter, Input, Output, signal, ViewChild, inject, input, output } from '@angular/core';
import { NgClass } from '@angular/common';
import { CommonModule } from '@angular/common';
import { AppButtonComponent } from '../../../button/app-button.component';


@Component({
  selector: 'drag-drop-input',
  standalone: true,
  templateUrl: './drag-drop-input.component.html',
  imports: [
    NgClass,
    CommonModule,
    AppButtonComponent
  ]
})
export class DragDropInputComponent {

  allowedExtensions = input<string[]>(['xlsx', 'xls', 'pdf', 'jpg', 'png']);
  label = input<string>('Arrastra tu archivo aquí');
  showDownload = input<boolean>(true);
  showClose = input<boolean>(true);
  maxFileSizeMB = input<number>(10);
  acceptAttribute = input<string>('*');

  protected _value = signal<File | null>(null);
  protected _fileExist = signal<boolean>(false);
  protected _fileName = signal<string>('Seleccione un archivo a subir');
  protected _isDisabled = signal<boolean>(false);
  protected _isDragging = signal<boolean>(false);
  protected _allowedMimeTypes = signal({
    'pdf': 'application/pdf',
    'csv': 'application/vnd.ms-excel',
    'xls': 'application/vnd.ms-excel',
    'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'png': 'image/png',
    'jpeg': 'image/jpeg',
    'webp': 'image/webp',
    'gif': 'image/gif'
  });

  protected onError = output<string>(); // error event to emit for upper components
  protected onSucces = output<string>(); // error event to emit for upper components

  /**
   * 
   * @param event 
   */
  protected onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this._isDragging.set(true);
  }

  /**
   * 
   * @param event 
   */
  protected onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this._isDragging.set(false);
  }

  /**
   * 
   * @param event 
   */
  protected onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this._isDragging.set(false);
    const file = event.dataTransfer?.files[0];
    
    if (!file) return
    this.onUpload(file);
  }



  // FILE SELECTOR LOGIC FROM HERE //


  /**
   * Retrives the current selected file by the user.
   * @returns Selected file.
   */
  getFile(): File | undefined {
    return this._value() ?? undefined;
  }

  /**
   * Set the component file value.
   * @param file File to set
   */
  setFile(file: File): void {
    this._value.set(file);
    this._fileName.set(file.name);
    this._fileExist.set(true);
  }

  /**
   * Set the component file name.
   * @param name 
   */
  setFileName(name: string): void {
    this._fileName.set(name);
    this._fileExist.set(true);
  }

  /**
   * Handles input file upload.
   * @param event Triggered upload event.
   */
  handleFileUpload(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;
    const value = input.files[0];
    this.onUpload(value);
  }

  /**
   * Helper function trigered when a file is selected via file selector.
   * @param event Trigered event.
   */
  protected onUpload(value: File): void {
    try {
      this.checkExtension(value);
      this.checkFileMimeType(value);
      this._value.set(value);
      this._fileName.set(value.name);
      this._fileExist.set(true);
      this.onSucces.emit('Archivo anclado correctamente');
    }
    catch (error) {
      if (error instanceof Error) {
        this.cleanSelectedFile();
        this.onError.emit(error.message);
      }
    }
  }

  /**
   * Checks if a file extension is valid by the '.extension' given in
   * the file name.
   * @param file Input file of 
   * @throws Error in case of any matching extension
   */
  private checkExtension(file: File): void {
    if (!file) return;
    if (this.allowedExtensions().length <= 0) return;

    const fileExtension = file.name.toLowerCase().split('.').pop();
    const isValidExtension = this.allowedExtensions().includes(fileExtension || '');

    if (!isValidExtension) {
      throw new Error(`Solo se permiten archivos con extensiones  ${this.allowedExtensions().join(', ')}`);
    }
  }

  /**
   * Verifyes the mime type of the uploaded or droped file.
   * @param file Selected file.
   */
  private checkFileMimeType(file: File) {
    if (!file) return;
    if (this.allowedExtensions().length <= 0) return;

    // Verificar si el MIME type del archivo corresponde a una extensión permitida
    const fileMimeType = file.type;
    const allowedMimeTypesMap = this._allowedMimeTypes();

    // Obtener los MIME types permitidos para las extensiones válidas
    const allowedMimeTypesForExtensions = this.allowedExtensions()
      .map(ext => allowedMimeTypesMap[ext as keyof typeof allowedMimeTypesMap])
      .filter(mime => mime !== undefined);

    if (!allowedMimeTypesForExtensions.includes(fileMimeType)) {
      throw new Error(`Tipo de archivo no válido. El archivo no corresponde a las extensiones permitidas.`);
    }

    return true;
  }

  /**
   * Event that recives the download event and delivers it to the handler.
   * @param event Triggered event.
   */
  onDownload(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.handleFileDownload();
  }

  /**
   * Helper function to download a file locally.
   */
  private handleFileDownload(): void {
    if (!this._value()) {
      return
    }
    // Descargar el archivo localmente
    const file = this._value()!;
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
  }

  /**
   * Event that recives the clean event and delivers it to the handler.
   * @param event Triggered event.
   */
  onSelectionClean(event: Event): void {
    event.preventDefault();
    this.cleanSelectedFile();
  }

  /**
   * Cleans the selected file visually
   */
  cleanSelectedFile(): void {
    this._value.set(null);
    this._fileExist.set(false);
    this._fileName.set('Seleccione un archivo a subir');
  }


}