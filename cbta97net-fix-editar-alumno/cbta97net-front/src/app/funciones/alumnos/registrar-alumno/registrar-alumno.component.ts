import { Component, inject, signal, ViewChild } from "@angular/core";
import { firstValueFrom } from 'rxjs';
import { FormularioImagenComponente } from '@funciones/alumnos/registrar-alumno/componentes/formulario-imagen/formulario-imagen.component';
import { AppButtonComponent } from '@/shared/components/composed/button/app-button.component';
import { FormularioInfoAlumnoComponente } from './componentes/formulario-info-alumno/formulario-info-alumno.component';
import { FormularioDocumentosComponente } from './componentes/formulario-documentos/formulario-documentos.component';
import { AlumnoServicio } from "../alumno.service";
import { ZardAlertDialogService } from '@shared/components/base/alert-dialog/alert-dialog.service';
import { AlumnoGuardar } from "@/nucleo/modelos/alumnos/alumno-guardar.model";
import { toastError, toastSuccess } from "@/shared/utils/toast"; 

/**
 * Componente encargado de gestionar el registro completo de un nuevo alumno.
 * 
 * Este componente coordina el proceso de registro que incluye:
 * - Registro de información básica del alumno
 * - Subida de foto escolar
 * - Subida de documentos (acta de nacimiento, certificado de secundaria, CURP)
 * 
 * El proceso de registro se realiza de forma secuencial: primero se registra el alumno
 * y luego se ejecutan en paralelo las subidas de archivos asociados.
 * 
 * @class RegistrarAlumnoComponente
 */
@Component({
  templateUrl: './registrar-alumno.component.html',
  imports: [
    AppButtonComponent,
    FormularioInfoAlumnoComponente,
    FormularioDocumentosComponente,
    FormularioImagenComponente,
  ]
})
export class RegistrarAlumnoComponente {

  @ViewChild('formularioImagen') formularioImagen!: FormularioImagenComponente;
  @ViewChild('formularioAlumnos') formularioAlumnos!: FormularioInfoAlumnoComponente;
  @ViewChild('formularioDocumentos') formularioDocumentos!: FormularioDocumentosComponente;
  @ViewChild('botonRegistrar') botonRegistrar!: AppButtonComponent;

  private servicioAlumnos: AlumnoServicio = inject(AlumnoServicio);
  private alertDialogService = inject(ZardAlertDialogService);  
  
  protected _estaAlumnoRegistrado = signal<boolean>(false);
  protected _estaImagenRegistrada = signal<boolean>(false);
  protected _estaActaNacimientoRegistrada = signal<boolean>(false);
  protected _estaCertificadoSecundariaRegistrada = signal<boolean>(false);
  protected _estaCurpRegistrada = signal<boolean>(false);


  /**
   * Abre el modal de confirmación de registro.
   * 
   * Muestra un diálogo al usuario para confirmar que desea proceder
   * con el registro del alumno antes de ejecutar el proceso completo.
   */
  manejarRegistroAlumno(event: Event) {
    event.preventDefault();
    event.stopPropagation();

    //establecemos el boton en estado de carga para evitar doble registro.
    this.botonRegistrar.isLoading(false);

    //validaciones previas antes de confirmacion si hay errores devolvera true
    if (this.formularioAlumnos.validarCamposEntrada()) {
      toastError('Error de registro', 'Error en campos de formulario verifique y intente de nuevo');
      return // en caso de error en formularios se retorna
    }

    this.alertDialogService.confirm({
      zTitle: 'Confirmar registro de alumno',
      zDescription: '¿Estás seguro de que deseas registrar al alumno(a)?',
      zOkText: 'Continuar',
      zCancelText: 'Cancelar',
      zMaskClosable: true,
      zOnOk: () => {
        this.registrarAlumno();
      }
    });
  }

  /**
   * Maneja el proceso completo de registro del alumno.
   * 
   * Esta función coordina todo el flujo de registro:
   * 1. Cierra el modal de confirmación
   * 2. Valida los campos del formulario (vacíos, longitud, fechas)
   * 3. Si la validación es exitosa, registra primero el alumno
   * 4. Luego ejecuta en paralelo el registro de imagen y documentos
   * 5. Limpia los campos si todo fue exitoso
   * 6. Muestra mensajes de éxito o error según corresponda
   * 
   * @throws {Error} Lanza un error si ocurre algún problema durante el registro
   */
  async registrarAlumno() {

    try {
      this.botonRegistrar.isLoading(true);
      // Esperar a que los datos se registren primero se registre primero
      await this.registrarDatosAlumno();

      // Una vez registrado el alumno, ejecutar las demás funciones en paralelo
      await Promise.all([
        this.registrarImagen(),
        this.registrarActaNacimiento(),
        this.registrarCertificadoSecundaria(),
        this.registrarCurp()
      ]);

      this.limpiarCampos();
      toastSuccess('Registro existoso', 'Alumno registrado correctamente');

    } catch (error: any) {
      this.botonRegistrar.isLoading(false); //forzar la disponibilidad del boton en errores.
      toastError('Error de registro', error.message);
    } finally {
      this.botonRegistrar.isLoading(false); //forzar la disponibilidad del boton.
    }
  }

  /**
   * Registra la información básica del alumno en el sistema.
   * 
   * Obtiene los datos del formulario de información del alumno y los envía
   * al servicio para su registro. Si el alumno ya fue registrado previamente
   * o no hay datos válidos, la función retorna sin hacer nada.
   * 
   * @returns {Promise<void>} Promesa que se resuelve cuando el registro es exitoso
   * @throws {Error} Lanza un error si falla el registro en el servicio
   */
  async registrarDatosAlumno(): Promise<void> {
    try {
      if (this._estaAlumnoRegistrado()) return;
      const alumno: AlumnoGuardar = this.formularioAlumnos.obtenerAlumno();

      
      if (!alumno) {
        this._estaAlumnoRegistrado.set(true);
        return
      }

      const data = await firstValueFrom(this.servicioAlumnos.registrarAlumno(alumno));
      console.log(data);
      this._estaAlumnoRegistrado.set(true) //marcar el registro de alumno como exitoso.
    }
    catch (error: any) {
      console.log(error);
      throw new Error(error.message);
    }
  }

  /**
   * Registra la foto escolar del alumno.
   * 
   * Obtiene la matrícula del alumno y la imagen seleccionada del formulario,
   * luego la envía al servicio para su almacenamiento. Si la imagen ya fue
   * registrada o no hay datos válidos, la función retorna sin hacer nada.
   * 
   * @returns {Promise<void>} Promesa que se resuelve cuando la imagen es registrada exitosamente
   * @throws {Error} Lanza un error si falla el registro de la imagen en el servicio
   */
  async registrarImagen(): Promise<void> {
    if (this._estaImagenRegistrada()) return;
    const matriculaAlumno: string = this.formularioAlumnos.obtenerMatriculaAlumno();
    const imagenAlumno: File | undefined = this.formularioImagen.obtenerImagenSeleccionada();
    console.log(imagenAlumno?.name);

    
    if (!matriculaAlumno || !imagenAlumno) {
      this._estaImagenRegistrada.set(true);
      return
    }

    try {
      const data = await firstValueFrom(this.servicioAlumnos.guardarFotoEscolar(matriculaAlumno, imagenAlumno));
      console.log('Imagen registrada:', data)
      this._estaImagenRegistrada.set(true);
    } catch (error: any) {
      console.log(error);
      throw new Error(error.message);
    }
  }

  /**
   * Registra el acta de nacimiento del alumno.
   * 
   * Obtiene la matrícula del alumno y el archivo del acta de nacimiento del formulario,
   * luego lo envía al servicio para su almacenamiento. Si el documento ya fue
   * registrado o no hay datos válidos, la función retorna sin hacer nada.
   * 
   * @returns {Promise<void>} Promesa que se resuelve cuando el acta es registrada exitosamente
   * @throws {Error} Lanza un error si falla el registro del documento en el servicio
   */
  async registrarActaNacimiento(): Promise<void> {
    if (this._estaActaNacimientoRegistrada()) return;
    const matriculaAlumno: string = this.formularioAlumnos.obtenerMatriculaAlumno();
    const actaNacimiento: File | undefined = this.formularioDocumentos.obtenerActaNacimiento();
    console.log(actaNacimiento?.name);
    
    
    if (!matriculaAlumno || !actaNacimiento) {
      this._estaActaNacimientoRegistrada.set(true);
      return
    }

    try {
      const data = await firstValueFrom(this.servicioAlumnos.guardarActaNacimiento(matriculaAlumno, actaNacimiento));
      console.log('Acta de nacimiento registrada:', data)
      this._estaActaNacimientoRegistrada.set(true);
    } catch (error: any) {
      console.log(error);
      throw new Error(error.message);
    }
  }

  /**
   * Registra el certificado de secundaria del alumno.
   * 
   * Obtiene la matrícula del alumno y el archivo del certificado de secundaria del formulario,
   * luego lo envía al servicio para su almacenamiento. Si el documento ya fue
   * registrado o no hay datos válidos, la función retorna sin hacer nada.
   * 
   * @returns {Promise<void>} Promesa que se resuelve cuando el certificado es registrado exitosamente
   * @throws {Error} Lanza un error si falla el registro del documento en el servicio
   */
  async registrarCertificadoSecundaria(): Promise<void> {
    if (this._estaCertificadoSecundariaRegistrada()) return;
    const matriculaAlumno: string = this.formularioAlumnos.obtenerMatriculaAlumno();
    const certificadoSecundaria: File | undefined = this.formularioDocumentos.obtenerCertificadoSecundaria();
    console.log(certificadoSecundaria?.name);

    
    if (!matriculaAlumno || !certificadoSecundaria) {
      this._estaCertificadoSecundariaRegistrada.set(true);
      return
    }

    try {
      const data = await firstValueFrom(this.servicioAlumnos.guardarCertificadoSecundaria(matriculaAlumno, certificadoSecundaria));
      console.log('Certificado de secundaria registrado:', data)
      this._estaCertificadoSecundariaRegistrada.set(true);
    } catch (error: any) {
      console.log(error);
      throw new Error(error.message);
    }
  }

  /**
   * Registra el documento CURP del alumno.
   * 
   * Obtiene la matrícula del alumno y el archivo CURP del formulario,
   * luego lo envía al servicio para su almacenamiento. Si el documento ya fue
   * registrado o no hay datos válidos, la función retorna sin hacer nada.
   * 
   * @returns {Promise<void>} Promesa que se resuelve cuando el CURP es registrado exitosamente
   * @throws {Error} Lanza un error si falla el registro del documento en el servicio
   */
  async registrarCurp(): Promise<void> {
    if (this._estaCurpRegistrada()) return;
    const matriculaAlumno: string = this.formularioAlumnos.obtenerMatriculaAlumno();
    const curp: File | undefined = this.formularioDocumentos.obtenerCurp();
    console.log(curp?.name);
    
    if (!matriculaAlumno || !curp) {
      this._estaCurpRegistrada.set(true);
      return
    }

    try {
      const data = await firstValueFrom(this.servicioAlumnos.guardarDocumentoCURP(matriculaAlumno, curp));
      console.log('CURP registrada:', data)
      this._estaCurpRegistrada.set(true);
    } catch (error: any) {
      console.log(error);
      throw new Error(error.message);
    }
  }

  /**
   * Limpia los campos de entrada una vez que se ha realizado un registro exitoso.
   * 
   * Esta función resetea todos los formularios y archivos seleccionados después
   * de un registro exitoso. Limpia:
   * - Campos de información del alumno
   * - Imagen seleccionada
   * - Documentos (acta de nacimiento, certificado de secundaria, CURP)
   * - Matrícula del alumno (si todos los procesos fueron completados)
   * 
   * También resetea los estados de registro (signals) para permitir un nuevo registro.
   */
  limpiarCampos() {
    if (this._estaAlumnoRegistrado()) {
      this.formularioAlumnos.limpiarCamposEntrada()
      this._estaAlumnoRegistrado.set(false);
    };
    if (this._estaImagenRegistrada()) {
      this.formularioImagen.limpiarArchivoSeleccionado();
      this._estaImagenRegistrada.set(false);
    }
    if (this._estaActaNacimientoRegistrada()) {
      this.formularioDocumentos.limpiarActaNacimiento();
      this._estaActaNacimientoRegistrada.set(false);
    }
    if (this._estaCertificadoSecundariaRegistrada()) {
      this.formularioDocumentos.limpiarCertificadoSecundaria();
      this._estaCertificadoSecundariaRegistrada.set(false);
    }
    if (this._estaCurpRegistrada()) {
      this.formularioDocumentos.limpiarCurp();
      this._estaCurpRegistrada.set(false);
    }

    //Si todas todos los procesos fueron completados, nos movemos al siguiente
    if (!this._estaAlumnoRegistrado() &&
      !this._estaImagenRegistrada() &&
      !this._estaActaNacimientoRegistrada() &&
      !this._estaCertificadoSecundariaRegistrada() &&
      !this._estaCurpRegistrada()) {

      //limpiamos el id
      this.formularioAlumnos.limpiarMatricula();
    }
  }

}