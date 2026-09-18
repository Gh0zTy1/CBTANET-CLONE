import { Component, ViewChild } from "@angular/core";
import { InputUploadSecondaryComponent } from '@/shared/components/composed/inputs/inline/input-upload-secondary/input-upload-secondary.component';
import { ZardDividerComponent } from '@/shared/components/base/divider/divider.component';

@Component({
  selector: 'formulario-documentos',
  templateUrl: './formulario-documentos.component.html',
  imports: [
    InputUploadSecondaryComponent,
    ZardDividerComponent
  ]
})
export class FormularioDocumentosComponente {

  @ViewChild('actaNacimiento') private documentoActaNacimientoInput!: InputUploadSecondaryComponent;
  @ViewChild('certificadoSecundaria') private documentoCertificadoSecundariaInput!: InputUploadSecondaryComponent;
  @ViewChild('documentoCurp') private documentoCurpInput!: InputUploadSecondaryComponent;

  obtenerActaNacimiento(): File | undefined {
    return this.documentoActaNacimientoInput.getSelectedFile();
  }

  obtenerCertificadoSecundaria(): File | undefined {
    return this.documentoCertificadoSecundariaInput.getSelectedFile();
  }

  obtenerCurp(): File | undefined {
    return this.documentoCurpInput.getSelectedFile();
  }

  limpiarActaNacimiento() {
    this.documentoActaNacimientoInput.cleanSelectedFile();
  }

  limpiarCertificadoSecundaria() {
    this.documentoCertificadoSecundariaInput.cleanSelectedFile();
  }

  limpiarCurp() {
    this.documentoCurpInput.cleanSelectedFile();
  }

}