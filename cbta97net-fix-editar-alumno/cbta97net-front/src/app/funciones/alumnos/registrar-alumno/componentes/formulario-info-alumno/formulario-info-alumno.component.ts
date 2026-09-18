import { NgStyle } from "@angular/common";
import { Component, signal, ViewChild } from "@angular/core";
import { InputTextOutlineComponent } from '@/shared/components/composed/inputs/outline/input-text-outline/input-text-outline.component';
import { InputDateOutlineComponent } from '@/shared/components/composed/inputs/outline/input-date-outline/input-date-outline.component';
import { InputNumberOutlineComponent } from '@/shared/components/composed/inputs/outline/input-number-outline/input-number-outline.component';
import { InputCellphoneOutlineComponent } from '@/shared/components/composed/inputs/outline/input-cellphone-outline/input-cellphone-outline.component';
import { InputInsuranceOutlineComponent } from '@/shared/components/composed/inputs/outline/input-insurance-outline/input-insurance-outline.component';
import { CheckboxComponent } from '@/shared/components/base/checkbox/checkbox.component';
import { TextAreaComponent } from '@/shared/components/composed/inputs/text-area/text-area.component';
import { ZardDividerComponent } from '@/shared/components/base/divider/divider.component';
import { AppComboboxComponent } from "@/shared/components/composed/combobox/app-combobox.component"; 
import { ZardComboboxOption } from "@/shared/components/base/combobox/combobox.component";
import { AlumnoGuardar, Direccion, Tutor } from "@/nucleo/modelos/alumnos/alumno-guardar.model";
import { normalize } from "@/shared/utils/object-sanitizer";

/**
 * Componente de formulario para capturar la información básica del alumno y su tutor legal.
 * 
 * Este componente gestiona:
 * - Campos de información personal del alumno (matrícula, CURP, nombres, apellidos, fecha de nacimiento, NSS, póliza de seguro)
 * - Campos de información del tutor legal (nombres, apellidos, teléfono, fecha de nacimiento)
 * - Validaciones de campos vacíos, longitud de entradas y fechas
 * - Condición especial del alumno (con área de texto opcional)
 * 
 * Proporciona métodos para obtener los datos del alumno, validar el formulario,
 * limpiar campos y gestionar el estado de errores.
 * 
 * @class FormularioInfoAlumnoComponente
 */
@Component({
  selector: 'formulario-info-alumnos',
  templateUrl: './formulario-info-alumno.component.html',
  imports: [
    NgStyle,
    InputTextOutlineComponent,
    InputDateOutlineComponent,
    InputNumberOutlineComponent,
    CheckboxComponent,
    InputCellphoneOutlineComponent,
    TextAreaComponent,
    InputInsuranceOutlineComponent,
    ZardDividerComponent,
    AppComboboxComponent
  ]
})
export class FormularioInfoAlumnoComponente {

  // ViewChild para inputs del alumno
  @ViewChild('matricula') private matriculaInput!: InputNumberOutlineComponent;
  @ViewChild('curp') private curpInput!: InputTextOutlineComponent;
  @ViewChild('nombres') private nombresInput!: InputTextOutlineComponent;
  @ViewChild('apellidoPaterno') private apellidoPaternoInput!: InputTextOutlineComponent;
  @ViewChild('apellidoMaterno') private apellidoMaternoInput!: InputTextOutlineComponent;
  @ViewChild('fechaNacimiento') private fechaNacimientoInput!: InputDateOutlineComponent;
  @ViewChild('nss') private nssInput!: InputNumberOutlineComponent;
  @ViewChild('polizaSeguro') private polizaSeguroInput!: InputInsuranceOutlineComponent;
  @ViewChild('condicionEspecial') private condicionEspecialInput!: TextAreaComponent;

  // ViewChild para inputs direccion del alumno
  @ViewChild('calle') private calle!: InputTextOutlineComponent;
  @ViewChild('colonia') private colonia!: InputTextOutlineComponent;
  @ViewChild('numeroExterior') private numeroExterior!: InputNumberOutlineComponent;
  @ViewChild('codigoPostal') private codigoPostal!: InputNumberOutlineComponent;
  @ViewChild('localidad') private localidad!: InputTextOutlineComponent;

  // ViewChild para inputs del tutor
  @ViewChild('parentesco') private parentesco!: AppComboboxComponent;
  @ViewChild('nombresTutor') private nombresTutorInput!: InputTextOutlineComponent;
  @ViewChild('apellidoPaternoTutor') private apellidoPaternoTutorInput!: InputTextOutlineComponent;
  @ViewChild('apellidoMaternoTutor') private apellidoMaternoTutorInput!: InputTextOutlineComponent;
  @ViewChild('telefonoTutor') private telefonoTutorInput!: InputCellphoneOutlineComponent;
  @ViewChild('fechaNacimientoTutor') private fechaNacimientoTutorInput!: InputDateOutlineComponent;

  maxDate = new Date().toLocaleDateString('en-CA');

  parentescos: ZardComboboxOption[] = [
    { value: 'PADRE', label: 'Padre/Madre' },
    { value: 'ABUELO', label: 'Abuelo(a)' },
    { value: 'HERMANO', label: 'Hermano(a)' },
    { value: 'TIO', label: 'Tío(a)' },
    { value: 'PRIMO', label: 'Primo(a)' },
    { value: 'TUTOR', label: 'Tutor(a)' }
  ];

  /**
   * Obtiene todos los datos del formulario y los estructura en un objeto Alumno.
   * 
   * Recopila la información de todos los campos del formulario (alumno y tutor legal)
   * y crea un objeto Alumno completo. Si hay información del tutor legal, se incluye
   * en el objeto; de lo contrario, se establece como undefined.
   * 
   * @returns {Alumno} Objeto Alumno con toda la información capturada en el formulario
   */
  obtenerAlumno(): AlumnoGuardar {
    // Obtener valores de los inputs del alumno
    const matricula = String(this.matriculaInput.getValue());
    const curp = this.curpInput.getValue();
    const nombres = this.nombresInput.getValue();
    const apellidoPaterno = this.apellidoPaternoInput.getValue();
    const apellidoMaterno = this.apellidoMaternoInput.getValue();
    const fechaNacimiento = this.fechaNacimientoInput.getValue();
    const nss = !this.nssInput.getValue() ? null : this.nssInput.getValue(); // validate if requires conversion to null
    const polizaSeguro = !this.polizaSeguroInput.getValue() ? null : this.polizaSeguroInput.getValue();
    const condicionEspecial = this.condicionEspecialInput.getValue();

    const calle = this.calle.getValue();
    const colonia = this.colonia.getValue();
    const numeroExterior = !this.numeroExterior.getValue() ? null : this.numeroExterior.getValue();
    const codigoPostal = !this.codigoPostal.getValue() ? null : this.codigoPostal.getValue();
    const localidad = this.localidad.getValue();

    const parentesco = this.parentesco.getValue();
    const nombresTutor = this.nombresTutorInput.getValue();
    const apellidoPaternoTutor = this.apellidoPaternoTutorInput.getValue();
    const apellidoMaternoTutor = this.apellidoMaternoTutorInput.getValue();
    const telefonoTutor = this.telefonoTutorInput.getValue();
    const fechaNacimientoTutor = this.fechaNacimientoTutorInput.getValue();
    
    let tutor: Tutor = {
      parentesco: parentesco,
      nombre: nombresTutor,
      apellido_paterno: apellidoPaternoTutor,
      apellido_materno: apellidoMaternoTutor,
      telefono: telefonoTutor,
      fecha_nacimiento: fechaNacimientoTutor
    };

    let direccion: Direccion = {
      calle: calle,
      colonia: colonia,
      numero_exterior: numeroExterior,
      codigo_postal: codigoPostal,
      localidad: localidad,
    };

    // Crear y retornar objeto Alumno
    const alumno: AlumnoGuardar = {
      matricula: matricula,
      curp: curp!,
      nombre: nombres!,
      apellido_paterno: apellidoPaterno!,
      apellido_materno: apellidoMaterno!,
      fecha_nacimiento: fechaNacimiento!,
      nss: nss,
      poliza_seguro: polizaSeguro,
      condicion_especial_desc: condicionEspecial,
      tutor_legal: tutor,
      direccion: direccion
    };

    return normalize(alumno) // normalizando antes de enviar;
  }

  /**
   * Valida el campo de matrícula.
   * Verifica que no esté vacío y tenga la longitud correcta según el máximo declarado en props.
   * @returns true si es hay errores, false si no hay errores
   */
  private validarMatricula(): boolean {
    const valor: string = String(this.matriculaInput.getValue()) ?? '';

    if (valor.toString().length < 14) {
      this.matriculaInput.showError(`La matrícula debe tener 14 caracteres`);
      return true;
    }
    return false;
  }

  /**
   * Valida el campo de CURP.
   * Verifica que tenga la longitud correcta según el máximo declarado en props.
   * El CURP es opcional.
   * @returns true si es hay errores, false si no hay errores
   */
  private validarCurp(): boolean {
    const valor = this.curpInput.getValue() ?? '';
    const maxLength = parseInt(this.curpInput.max());

    if (valor.length < maxLength) {
      this.curpInput.showError(`La CURP debe tener ${maxLength} caracteres`);
      return true;
    }

    const regex = /^[A-Z]{4}\d{6}[HM][A-Z]{5}[A-Z\d]\d$/;
    if(!regex.test(valor)) {
      this.curpInput.showError(`Formato de CURP invalido`);
      return true;
    }
    
    return false;
  }

  /**
   * Valida que el nombre(s) del alumno este presente ya que es estricto
   * @returns true si es hay errores, false si no hay errores
   */
  private validarNombresAlumno(): boolean {
    const nombres = this.nombresInput.getValue() ?? '';
    if(!nombres) {
      this.nombresInput.showError(`Los nombres no pueden estar vacios`);
      return true
    }
    return false
  }

  /**
   * Valida que el apellido materno este presente ya que es estricto
   * @returns true si es hay errores, false si no hay errores
   */
  private validarApellidoMaternoAlumno(): boolean {
    const apellidoMaterno = this.apellidoMaternoInput.getValue() ?? '';
    if(!apellidoMaterno) {
      this.apellidoMaternoInput.showError(`El apellido no puede estar vacio`);
      return true
    }
    return false
  }

  /**
   * Valida que el apellido paterno este presente ya que es estricto
   * @returns true si es hay errores, false si no hay errores
   */
  private validarApellidoPaternoAlumno(): boolean {
    const apellidoPaterno = this.apellidoPaternoInput.getValue() ?? '';
    if(!apellidoPaterno) {
      this.apellidoPaternoInput.showError(`El apellido no puede estar vacio`);
      return true
    }
    return false
  }


  /**
   * Valida el campo de NSS.
   * Verifica que tenga la longitud correcta según el máximo declarado en props.
   * El NSS es opcional.
   * @returns true si es hay errores, false si no hay errores
   */
  private validarNss(): boolean {
    const valor = this.nssInput.getValue();

    if (!valor) return false; // NSS es opcional

    const maxLength = parseInt(this.nssInput.max());
    if (valor.toString().length < maxLength) {
      this.nssInput.showError(`El NSS debe tener ${maxLength} caracteres`);
      return true;
    }
    return false;
  }

  /**
   * Valida el campo de póliza de seguro.
   * Verifica que tenga la longitud correcta según el máximo declarado en props.
   * La póliza es opcional.
   * @returns true si es hay errores, false si no hay errores
   */
  private validarPolizaSeguro(): boolean {
    const valor = String(this.polizaSeguroInput.getValue());
    const MAXLENGHT = 10; // originalmente solo son 10 caracteres, pero se establece a dos por las dos barras diagonales (xxx/xxx/xxxx)
    
    if (!valor || valor === '0') return false; // poliza es opcional

    if (valor.length < MAXLENGHT) {
      this.polizaSeguroInput.showError(`La póliza debe tener 10 numeros (xxx/xxx/xxxx)`);
      return true;
    }
    return false;
  }

  /**
   * Valida el campo de fecha de nacimiento del alumno.
   * Verifica que no sea una fecha futura y que esté completa.
   * @returns true si es hay errores, false si no hay errores.
   */
  private validarFechaNacimiento(): boolean {
    const fechaNacimiento = this.fechaNacimientoInput.getValue(); //fecha de nacimiento del alumno

    if (this.fechaNacimientoInput.isValueUncomplete()) {
      this.fechaNacimientoInput.showError('Formato de fecha incorrecto');
      return true;
    }

    if (fechaNacimiento && fechaNacimiento > new Date()) {
      this.fechaNacimientoInput.showError('La fecha seleccionada no puede ser mayor al día de hoy');
      return true;
    }
    return false;
  }

  /**
   * Valida el numero de telefono del tutor legal
   */
  private validarTelefonoTutor(): boolean {
    // Teléfono tutor
    const telefono = this.telefonoTutorInput.getValue();

    if (!telefono) return false; // numero de telefono de tutor es opcional

    const MAXLENGHT = 10; // normalmente son 10 digitos para numeros nacionales
    if (telefono.toString().length < MAXLENGHT) {
      this.telefonoTutorInput.showError('El teléfono debe tener todos sus dígitos');
      return true;
    }
    return false;
  }

  /**
   * Valida los campos del tutor legal.
   * @returns true si es hay errores, false si no hay errores
   */
  private validarFechaNacimientoTutor(): boolean {
    const today = new Date();
    const fechaNacimientoTutor = this.fechaNacimientoTutorInput.getValue();

    if(!fechaNacimientoTutor) return false; // fecha de nacimiento de tutor es opcional

    // Fecha nacimiento tutor
    if (fechaNacimientoTutor && this.fechaNacimientoTutorInput.isValueUncomplete()) {
      this.fechaNacimientoTutorInput.showError('Formato de fecha incorrecto');
      return true;
    }

    if (fechaNacimientoTutor && fechaNacimientoTutor > today) {
      this.fechaNacimientoTutorInput.showError('La fecha seleccionada no puede ser mayor al día de hoy');
      return true;
    }

    return false;
  }

  /**
   * Valida todos los campos de entrada del formulario de alumno, estableciendo estados
   * de error globales y en cada input en caso de no cumplir sus condiciones.
   * @returns true si todos los campos son válidos
   */
  validarCamposEntrada(): boolean {
    // Limpiar errores previos
    this.limpiarErroresCamposEntrada();

    // Validar campos del alumno
    const matriculaValida = this.validarMatricula();
    const nombresValidos = this.validarNombresAlumno();
    const apellidoPaternoValido = this.validarApellidoPaternoAlumno();
    const apellidoMaternoValido = this.validarApellidoMaternoAlumno();
    const curpValido = this.validarCurp();
    const nssValido = this.validarNss();
    const polizaValida = this.validarPolizaSeguro();
    const fechaNacimientoValida = this.validarFechaNacimiento();
    const numeroTelefonoTutor = this.validarTelefonoTutor();
    const fechaNacimientoTutor = this.validarFechaNacimientoTutor();

    // Verificar si hay errores
    const hayErrores = matriculaValida || curpValido || nombresValidos || 
                        apellidoPaternoValido || apellidoMaternoValido
                        nssValido || polizaValida || fechaNacimientoValida ||
                        numeroTelefonoTutor || fechaNacimientoTutor;

    return hayErrores;
  }

  /**
   * Oculta todos los errores visuales de los campos de entrada.
   * 
   * Limpia los mensajes de error de todos los campos del formulario
   * (tanto del alumno como del tutor legal) sin modificar los valores ingresados.
   */
  limpiarErroresCamposEntrada() {
    this.matriculaInput.hideError();
    this.curpInput.hideError();
    this.nombresInput.hideError();
    this.apellidoPaternoInput.hideError();
    this.apellidoMaternoInput.hideError();
    this.fechaNacimientoInput.hideError();
    this.nssInput.hideError();
    this.polizaSeguroInput.hideError();

    //campos entrada tutor
    this.nombresTutorInput.hideError();
    this.apellidoPaternoTutorInput.hideError();
    this.apellidoMaternoTutorInput.hideError();
    this.telefonoTutorInput.hideError();
    this.fechaNacimientoTutorInput.hideError();
  }

  /**
   * Limpia todos los valores de los campos de entrada del formulario.
   * 
   * Establece todos los campos (tanto del alumno como del tutor legal)
   * a sus valores por defecto, dejando el formulario listo para una nueva captura.
   * No incluye la matrícula, que debe limpiarse por separado usando limpiarMatricula().
   */
  limpiarCamposEntrada() {
    this.curpInput.clearValue();
    this.nombresInput.clearValue();
    this.apellidoPaternoInput.clearValue();
    this.apellidoMaternoInput.clearValue();
    this.fechaNacimientoInput.clearValue();
    this.nssInput.clearValue();
    this.polizaSeguroInput.clearValue();

    //campos entrada tutor
    this.nombresTutorInput.clearValue();
    this.apellidoPaternoTutorInput.clearValue();
    this.apellidoMaternoTutorInput.clearValue();
    this.telefonoTutorInput.clearValue();
    this.fechaNacimientoTutorInput.clearValue();
  }

  /**
   * Limpia el valor del campo de matrícula del alumno.
   * 
   * Establece el campo de matrícula a su valor por defecto.
   * Esta función se usa por separado ya que la matrícula puede necesitar
   * limpiarse en un momento diferente al resto de los campos.
   */
  limpiarMatricula() {
    this.matriculaInput.clearValue();
  }

  /**
   * Obtiene la matrícula del alumno ingresada en el formulario.
   * 
   * @returns {string} La matrícula del alumno como cadena de texto
   */
  obtenerMatriculaAlumno(): string {
    return String(this.matriculaInput.getValue());
  }

  protected showingConditionTextArea = signal<boolean>(false);

  /**
   * Maneja el cambio de estado del checkbox de condición especial.
   * 
   * Muestra u oculta el área de texto para describir la condición especial
   * del alumno según el estado del checkbox.
   * 
   * @param {boolean} estado - true para mostrar el área de texto, false para ocultarla
   */
  protected alMarcarCheckboxCondicionEspecial(estado: boolean) {
    estado
      ? this.showingConditionTextArea.set(true)
      : this.showingConditionTextArea.set(false);
  }
}