import { Component, ElementRef, EventEmitter, inject, input, Output, signal, ViewChild } from '@angular/core';
import { AppButtonComponent } from '@/shared/components/composed/button/app-button.component';
import { NgClass } from '@angular/common';
import { ZardTooltipImports } from '@/shared/components/base/tooltip';
import { toastError } from '@/shared/utils/toast';


@Component({
  selector: 'input-upload',
  templateUrl: './input-upload.component.html',
  imports: [
    NgClass,
    AppButtonComponent,
    ZardTooltipImports
  ]
})
/**
 * InputUploadComponent 
 * 
 * is a reusable component that provides a styled component that allows file selection with
 * drag and drop or selection.
 */
export class InputUploadComponent {

  @ViewChild('file') private fileInput!: ElementRef<HTMLInputElement>;
  @ViewChild('saveFileButton') private saveFileButton!: AppButtonComponent;
  @ViewChild('downloadFileButton') private downloadFileButton!: AppButtonComponent;

  @Output() private download = new EventEmitter<Event>();
  @Output() private upload = new EventEmitter<Event>();

  validExtensions = input<string[]>([]);
  label = input<string>('');
  showUpload = input<boolean>(false);

  protected _value = signal<File | null>(null);
  protected _lastValue = signal<File | null>(null);
  protected _isFileSaved = signal<boolean>(false);
  protected readonly _inputId = signal<string>(`file-input-${typeof crypto !== 'undefined' && crypto.randomUUID ? crypto.randomUUID() : Math.random().toString(36).substring(2, 11)}`);
  protected _fileExist = signal<boolean>(false);
  protected _fileName = signal<string>('Seleccione un archivo a subir');
  protected _isDraggingOver = signal<boolean>(false);
  protected _isLoading = signal<boolean>(false);
  protected _isDisabled = signal<boolean>(false);
  protected _allowedMimeTypes = signal({
    'pdf': 'application/pdf',
    'csv': 'application/vnd.ms-excel',
    'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'png': 'image/png',
    'jpeg': 'image/jpeg',
    'webp': 'image/webp',
    'gif': 'image/gif'
  });


  /**
   * Handles the file upload event when a file is selected via the input element.
   * @param event The DOM event triggered by the file selection.
   */
  protected onUpload(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.handleFileSelection(file)
  }

  /**
   * Handles the file drop event when a file is dragged and dropped onto the component.
   * @param event The DragEvent triggered by the file drop.
   */
  protected onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this._isDraggingOver.set(false);
    const file = event.dataTransfer?.files[0];
    if (file) {
      this.handleFileSelection(file);
    }
  }

  /**
   * Handles the selection of a file, performing validation and updating component state.
   * @param file The selected File object.
   */
  private handleFileSelection(file: File) {
    try {
      // Validate extension and mime type before recording the file
      this.checkExtension(file);
      this.checkFileMimeType(file);
      if(this._value()) this._lastValue.set(this._value()); //set the current to last saved file
      this._value.set(file);
      this._fileName.set(file.name);
      this._fileExist.set(true); // show download button
      this._isFileSaved.set(false); // set the state of the file as not saved
    }
    catch (error) {
      if (error instanceof Error) {
        toastError('Error al subir el archivo', error.message)
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
   * Checks if the file's MIME type is among the allowed MIME types for the valid extensions.
   * @param file The file to check.
   * @returns True if the MIME type is valid, throws an error otherwise.
   * @throws Error if the file MIME type does not correspond to the allowed extensions.
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
   * Helper funtion to download a file locally.
   */
  downloadFile(file: File | null) {
    if(!file) return
    // Descargar el archivo localmente
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
   * Handles the download of the selected file. If a file is not locally selected but is marked as saved, it emits a download event.
   * Otherwise, it initiates a local download of the currently selected file.
   * @param event The DOM event triggered by the download action.
   */
  handleDownloadFile(event: Event) {
    event.preventDefault();
    event.stopPropagation();

    if (!this._isFileSaved()) {
      if (this._lastValue()) {
        this.downloadFile(this._lastValue());
        return
      }
      //there isnt a value and is not saved request the download
      this.download.emit();
      return
    }
    this.downloadFileButton.isLoading(true);
    if (this._value()) {
      this.downloadFile(this._value()!);
    }
    this.downloadFileButton.isLoading(false);
  }

  /**
   * Emits an upload event when the save file button is clicked, indicating that the selected file should be saved.
   * @param event The DOM event triggered by the save action.
   */
  protected onFileSave(event: Event) {
    if (!this._value()) return;
    this.upload.emit(event);
  }

  /**
   * Handles the `dragleave` event, resetting the drag-over state.
   * @param event The DragEvent triggered when a dragged item leaves the component.
   */
  protected onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this._isDraggingOver.set(false);
  }

  /**
   * Handles the `dragover` event, setting the drag-over state.
   * @param event The DragEvent triggered when a dragged item is over the component.
   */
  protected onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this._isDraggingOver.set(true);
  }

  /**
   * Returns the currently selected file or undefined if no file is selected.
   * @returns The selected File object or undefined.
   */
  getSelectedFile(): File | undefined {
    return this._value() ?? undefined;
  }

  /**
   * Sets the displayed file name and updates the file existence state to show the download button.
   * @param fileName The name of the file to display.
   */
  setFileName(fileName: string): void {
    if (!fileName) {
      this._fileName.set('Seleccione un archivo a subir');
      return;
    }
    this._fileName.set(fileName);
    this._fileExist.set(true) // show the download button
  }

  /**
   * Sets the selected file and updates the file name and existence state.
   * @param file The File object to set as the currently selected file.
   */
  setFile(file: File): void {
    if (!file) return;
    if(this._value()) this._lastValue.set(this._value()); //set the current to last saved file
    this._value.set(file); // assign the new one
    this._fileExist.set(true);
  }

  /**
   * Sets the saved state of the document.
   * @param state The boolean state to set.
   */
  isFileSaved(state: boolean) {
    this._isFileSaved.set(state);
  }

  /**
   * Sets the loading state for the save file button.
   * @param state The boolean state to set (true for loading, false for not loading).
   */
  isSaveButtonLoading(state: boolean) {
    this.saveFileButton.isLoading(state);
  }

  /**
   * Sets the loading state for the download file button.
   * @param state The boolean state to set (true for loading, false for not loading).
   */
  isDownloadButtonLoading(state: boolean) {
    this.downloadFileButton.isLoading(state);
  }

}
