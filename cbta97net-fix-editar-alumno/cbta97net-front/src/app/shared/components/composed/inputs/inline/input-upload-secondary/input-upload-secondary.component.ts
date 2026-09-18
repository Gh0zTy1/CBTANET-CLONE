import { Component, ElementRef, EventEmitter, inject, input, Output, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppButtonComponent } from '@/shared/components/composed/button/app-button.component';
import { ZardTooltipImports } from "@/shared/components/base/tooltip";
import { toastError } from '@/shared/utils/toast';


@Component({
  selector: 'input-upload-secondary',
  templateUrl: './input-upload-secondary.component.html',
  imports: [
    CommonModule,
    AppButtonComponent,
    ZardTooltipImports
  ]
})
/**
 * InputUploadSecondaryComponent 
 * 
 * is a reusable component that provides a styled component that allows file selection with
 * drag and drop or selection.
 */
export class InputUploadSecondaryComponent {

  @ViewChild('file') private fileInput!: ElementRef<HTMLInputElement>;
  @ViewChild('downloadFileButton') private downloadFileButton!: AppButtonComponent;

  @Output() private download = new EventEmitter<Event>();
  @Output() private upload = new EventEmitter<Event>();

  validExtensions = input<string[]>([]);
  label = input<string>('');

  protected _value = signal<File | null>(null);
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
      this._value.set(file);
      this._fileName.set(file.name);
      this.checkExtension(file);
      this.checkFileMimeType(file);
      this._fileExist.set(true); // show download button
    }
    catch (error) {
      if (error instanceof Error) {
        this._value.set(null);
        this.cleanSelectedFile();
        toastError('Error al subir al archivo', error.message);
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
   * Helper function to download a file locally.
   */
  private handleFileDownload() {
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
   * Handles the download of the selected file. If a file is not locally selected but is marked as saved, it emits a download event.
   * Otherwise, it initiates a local download of the currently selected file.
   * @param event The DOM event triggered by the download action.
   */
  protected onDownload(event: Event) {
    event.preventDefault();
    this.downloadFileButton.isLoading(true);
    this.handleFileDownload();
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
   * Handles the event when the selection is cleaned, preventing default behavior and calling `handleSelectionCleaning`.
   * @param event The DOM event triggered by the clean selection action.
   */
  protected onSelectionClean(event: Event) {
    event.preventDefault();
    this.handleSelectionCleaning();
  }

  /**
   * Cleans the currently selected file, resetting the input and related state.
   */
  cleanSelectedFile() {
    if (!this._value) return
    this.handleSelectionCleaning();
  }

  /**
   * Resets the file input, clears the selected file value, and updates the file name display.
   */
  private handleSelectionCleaning() {
    this._value.set(null);
    this._fileName.set('Seleccione un archivo a subir');
    this.fileInput.nativeElement.value = '';
    this._fileExist.set(false);
  }

  /**
   * Returns the currently selected file or undefined if no file is selected.
   * @returns The selected File object or undefined.
   */
  getSelectedFile(): File | undefined {
    return this._value() ?? undefined;
  }

  /**
   * Sets the loading state for the download file button.
   * @param state The boolean state to set (true for loading, false for not loading).
   */
  isDownloadButtonLoading(state: boolean) {
    this.downloadFileButton.isLoading(state);
  }

}