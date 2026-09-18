import { Component, EventEmitter, inject, input, Output, signal, ViewChild } from '@angular/core';
import { AppButtonComponent } from '@/shared/components/composed/button/app-button.component';
import { ZardTooltipImports } from '@/shared/components/base/tooltip';
import { toastError } from '@/shared/utils/toast';


@Component({
  selector: 'input-upload-inline',
  templateUrl: './input-upload-inline.component.html',
  imports: [
    AppButtonComponent,
    ZardTooltipImports
  ]
})
/**
 * InputUploadComponent 
 * 
 * Reusable component that provides an inline file selector that allows file selection and 
 * drag and drop features.
 */
export class InputUploadInlineComponent {

  @Output() private fileSelected = new EventEmitter<void>();
  @Output() private download = new EventEmitter<void>();
  @Output() private clean = new EventEmitter<void>();

  validExtensions = input<string[]>([]);
  showDownload = input<boolean>(true);
  showClose = input<boolean>(true);

  protected _value = signal<File | null>(null);
  protected _fileExist = signal<boolean>(false);
  protected _fileName = signal<string>('Seleccione un archivo a subir');
  protected _isError = signal<boolean>(false);
  protected _isDisabled = signal<boolean>(false);
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

  /**
   * Retrives the current selected file by the user.
   * @returns Selected file.
   */
  getFile(): File | undefined {
    return this._value() ?? undefined;
  }

  setFile(file: File) {
    this._value.set(file);
    this._fileName.set(file.name);
    this._fileExist.set(true);
  }

  setFileName(name: string) {
    this._fileName.set(name);
    this._fileExist.set(true);
  }

  /**
   * Helper function trigered when a file is selected via file selector.
   * @param event Trigered event.
   */
  protected onUpload(event: Event): void {
    try {
      const input = event.target as HTMLInputElement;
      if (!input.files || input.files.length === 0) return;

      const value = input.files[0];
      this.checkExtension(value);
      this.checkFileMimeType(value);
      this._value.set(value);
      this._fileName.set(value.name);
      this._fileExist.set(true);
      this.fileSelected.emit();
    }
    catch (error) {
      if (error instanceof Error) {
        this.cleanSelectedFile();
        toastError('Error al cargar el archivo', error.message)
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
    if (this.validExtensions().length <= 0) return;

    const fileExtension = file.name.toLowerCase().split('.').pop();
    const isValidExtension = this.validExtensions().includes(fileExtension || '');

    if (!isValidExtension) {
      throw new Error(`Solo se permiten archivos con extensiones  ${this.validExtensions().join(', ')}`);
    }
  }

  /**
   * Verifyes the mime type of the uploaded or droped file.
   * @param file Selected file.
   */
  private checkFileMimeType(file: File) {
    if (!file) return;
    if (this.validExtensions().length <= 0) return;

    // Verificar si el MIME type del archivo corresponde a una extensión permitida
    const fileMimeType = file.type;
    const allowedMimeTypesMap = this._allowedMimeTypes();

    // Obtener los MIME types permitidos para las extensiones válidas
    const allowedMimeTypesForExtensions = this.validExtensions()
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
  onDownload(event: Event) {
    event.preventDefault();
    this.handleFileDownload();
  }

  /**
   * Helper function to download a file locally.
   */
  private handleFileDownload() {
    if (!this._value()) {
      this.download.emit();
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
  onSelectionClean(event: Event) {
    event.preventDefault();
    this.cleanSelectedFile();
  }

  /**
   * Handler function trigered when a dragable elements enters the 
   * component area.
   */
  cleanSelectedFile() {
    this.clean.emit();
    this._value.set(null);
    this._fileExist.set(false);
    this._fileName.set('Seleccione un archivo a subir');
  }

  cleanInputFromExternalSource() {
    this._value.set(null);
    this._fileExist.set(false);
    this._fileName.set('Seleccione un archivo a subir');
  }

}