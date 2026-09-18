import { Component, inject, signal, ViewChild } from "@angular/core";
import { AppButtonComponent } from '@/shared/components/composed/button/app-button.component';
import { UsuarioServicio } from "@funciones/usuarios/usuarios.service";
import { InputTextOutlineComponent } from "@/shared/components/composed/inputs/outline/input-text-outline/input-text-outline.component";
import { InputCellphoneOutlineComponent } from '@/shared/components/composed/inputs/outline/input-cellphone-outline/input-cellphone-outline.component';
import { UsuarioGuardar } from "@/nucleo/modelos/usuarios/usuario-guardar.model";
import { InputPasswordOutlineComponent } from "@/shared/components/composed/inputs/outline/input-password-outline/input-password-outline.component";
import { ZardDividerComponent } from '@/shared/components/base/divider/divider.component';
import { AlertComposedComponent } from '@/shared/components/base/alert/alert.component';
import { ZardAlertDialogService } from '@shared/components/base/alert-dialog/alert-dialog.service';
import { normalize } from "@/shared/utils/object-sanitizer";
import { toastError, toastSuccess } from "@/shared/utils/toast";

/**
 * Componente Angular para el registro de nuevos usuarios en el sistema.
 * 
 * Este componente gestiona el formulario de registro de usuarios, incluyendo:
 * - Validación de campos (nombre, apellidos, CURP, teléfono, email, contraseña)
 * - Validación de formato de datos según reglas de negocio
 * - Comunicación con el servicio de usuarios para registrar nuevos usuarios
 * - Gestión de mensajes de éxito y error mediante PrimeNG
 * - Limpieza de campos y errores del formulario
 * 
 * @class RegistrarUsuarioComponente
 */
@Component({
    templateUrl: './registrar-usuario.component.html',
    imports: [
        AppButtonComponent,
        InputTextOutlineComponent,
        InputCellphoneOutlineComponent,
        InputPasswordOutlineComponent,
        ZardDividerComponent,
        AlertComposedComponent,
    ]
})
export class RegistrarUsuarioComponente {

        private alertDialogService = inject(ZardAlertDialogService);  

    @ViewChild('nombre') nombreInput!: InputTextOutlineComponent;
    @ViewChild('apellido_paterno') apellidoPaternoInput!: InputTextOutlineComponent;
    @ViewChild('apellido_materno') apellidoMaternoInput!: InputTextOutlineComponent;
    @ViewChild('curp') curpInput!: InputTextOutlineComponent;
    @ViewChild('telefono') telefonoInput!: InputCellphoneOutlineComponent;
    @ViewChild('email') emailInput!: InputTextOutlineComponent;
    @ViewChild('contrasena') contrasenaInput!: InputPasswordOutlineComponent;
    @ViewChild('confirmar_contrasena') confirmarContrasenaInput!: InputPasswordOutlineComponent;
    @ViewChild('boton_registrar') botonRegistrar!: AppButtonComponent;


    private isError = signal<boolean>(false); //global form error flag


    constructor(
        private readonly usuarioServicio: UsuarioServicio  
    ) { }

    /**
     * Método del ciclo de vida de Angular que se ejecuta después de la inicialización del componente.
     * Actualmente no realiza ninguna operación de inicialización.
     */
    ngOnInit() {

    }

    /**
     * Verifica que el CURP contenga el formato correcto.
     * @param curp Clave Unica de Registro de Población (CURP) del usuario.
     * @returns true si es valido.
     */
    private validarCurp(curp: string): boolean {
        const regex = /^[A-Z]{4}\d{6}[HM][A-Z]{5}[A-Z\d]\d$/;
        return regex.test(curp);
    }

    /**
     * Verifica que el nombre del usuario solo contenta letras.
     * @param nombre Nombre del usuario.
     * @returns true si es valido.
     */
    private validarNombreUsuario(nombre: string) {
        const regex = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,50}$/;
        return regex.test(nombre.trim());
    }

    /**
     * Verifica que el numero de telefono solo contenga entre 6 y 10 numeros consecutivos.
     * @param telefono Número telefónico del usuario.
     * @returns true si el teléfono es válido.
     */
    private validarTelefono(telefono: string) {
        const regex = /^[0-9]{6,10}$/;
        return regex.test(telefono.trim());
    }

    /**
     * Verifica que el texto ingresado corresponda al formato de un correo electrónico.
     * @param email Correo electrónico del usuario.
     * @returns true si es un correo electrónico válido.
     */
    private validarEmail(email: string) {
        const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        return regex.test(email);
    }

    /**
     * Obtiene los valores de todos los campos del formulario y construye un objeto RegistrarUsuario.
     * Extrae los valores de los campos de entrada mediante ViewChild y los organiza en un objeto
     * que puede ser enviado al servicio para registrar el usuario.
     * 
     * @returns Objeto UsuarioGuardar con los datos del formulario.
     */
    private obtenerUsuario(): UsuarioGuardar {

        let nombre: string = this.nombreInput.getValue() ?? '';
        let apellido_paterno: string = this.apellidoPaternoInput.getValue() ?? '';
        let apellido_materno: string = this.apellidoMaternoInput.getValue() ?? '';
        let curp: string = this.curpInput.getValue() ?? '';
        let telefono: string = this.telefonoInput.getValue() ?? '';
        let email: string = this.emailInput.getValue() ?? '';
        let contrasena: string = this.contrasenaInput.getValue() ?? '';

        let usuario: UsuarioGuardar = {
            nombre,
            apellido_paterno,
            apellido_materno,
            curp,
            telefono,
            email,
            contrasena
        };

        return usuario;
    }

    /**
     * Valida todos los campos del formulario de registro.
     * 
     * Realiza las siguientes validaciones:
     * - Verifica que todos los campos obligatorios estén completos
     * - Valida el formato del nombre y apellidos
     * - Valida el formato del CURP
     * - Valida el formato del teléfono
     * - Valida el formato del email
     * - Verifica que la contraseña tenga al menos 8 caracteres
     * - Verifica que las contraseñas coincidan
     * 
     * Si alguna validación falla, muestra un mensaje de error en el campo correspondiente
     * y establece la bandera de error global (isError) en true.
     */
    private validarFormulario() {
        const nombre: string | undefined = this.nombreInput.getValue()?.toUpperCase() ?? '';
        const apellidoPaterno: string | undefined = this.apellidoPaternoInput.getValue()?.toUpperCase() ?? '';
        const apellidoMaterno: string | undefined = this.apellidoMaternoInput.getValue()?.toUpperCase() ?? '';
        const curp: string | undefined = this.curpInput.getValue()?.trim().toUpperCase() ?? '';
        const telefono: string | undefined = this.telefonoInput.getValue()?.trim() ?? '';
        const email: string | undefined = this.emailInput.getValue()?.trim() ?? '';
        const contrasena = this.contrasenaInput.getValue() ?? '';
        const confirmarContrasena = this.confirmarContrasenaInput.getValue() ?? '';

        if (!this.validarNombreUsuario(nombre)) {
            const message = 'Nombre no válido (mínimo 2 letras, solo letras y espacios).';
            this.nombreInput.showError(message);
            this.isError.set(true);
        }

        if (!this.validarNombreUsuario(apellidoPaterno)) {
            const message = 'Apellido paterno no válido (mínimo 2 letras, solo letras y espacios.';
            this.apellidoPaternoInput.showError(message);
            this.isError.set(true);
        }

        if (!this.validarNombreUsuario(apellidoMaterno)) {
            const message = 'Apellido materno no válido (mínimo 2 letras, solo letras y espacios.';
            this.apellidoMaternoInput.showError(message);
            this.isError.set(true);
        }

        if (!this.validarCurp(curp)) {
            const message = 'El formato de CURP es incorrecto.';
            this.curpInput.showError(message);
            this.isError.set(true);
        }

        if (!this.validarTelefono(telefono)) {
            const message = 'El teléfono debe tener 10 dígitos.';
            this.telefonoInput.showError(message);
            this.isError.set(true);
        }

        if (!this.validarEmail(email)) {
            const message = 'Correo electrónico no válido.';
            this.emailInput.showError(message);
            this.isError.set(true);
        }

        if (contrasena.length < 8) {
            const message = 'La contraseña debe tener al menos 8 caracteres.';
            this.contrasenaInput.showError(message);
            this.isError.set(true);
        }

        if (contrasena !== confirmarContrasena) {
            const message = 'Las contraseñas no coinciden.';
            this.contrasenaInput.showError(message);
            this.confirmarContrasenaInput.showError(message);
            this.isError.set(true);
        }
    }


    /**
     * Limpia todos los valores ingresados en los campos del formulario.
     * Establece el valor de cada campo de entrada a una cadena vacía.
     * Útil después de un registro exitoso o para resetear el formulario.
     */
    public limpiarValoresEnCamposEntrada() {
        this.nombreInput.setValue('');
        this.apellidoPaternoInput.setValue('');
        this.apellidoMaternoInput.setValue('');
        this.curpInput.setValue('');
        this.emailInput.setValue('');
        this.telefonoInput.setValue('');
        this.contrasenaInput.setValue('');
        this.confirmarContrasenaInput.setValue('');
    }

    /**
     * Limpia todos los mensajes de error visuales de los campos del formulario.
     * Oculta los errores mostrados en cada campo de entrada y restablece
     * la bandera de error global (isError) a false.
     * Se ejecuta antes de realizar una nueva validación para evitar mostrar errores previos.
     */
    limpiarErroresCamposEntrada() {
        this.nombreInput.hideError();
        this.apellidoPaternoInput.hideError();
        this.apellidoMaternoInput.hideError();
        this.curpInput.hideError();
        this.emailInput.hideError();
        this.telefonoInput.hideError();
        this.contrasenaInput.hideError();
        this.confirmarContrasenaInput.hideError();
        this.isError.set(false);
    }

    /**
     * Método principal para registrar un nuevo usuario en el sistema.
     * 
     * Proceso de ejecución:
     * 1. Limpia todos los errores previos del formulario
     * 2. Valida todos los campos del formulario
     * 3. Si hay errores de validación, detiene el proceso
     * 4. Si la validación es exitosa, obtiene los datos del usuario
     * 5. Muestra el estado de carga en el botón de registro
     * 6. Envía la solicitud al servicio para registrar el usuario
     * 7. Muestra mensajes de éxito o error según el resultado
     * 8. Limpia el formulario si el registro fue exitoso
     * 
     * @throws Muestra mensajes de error mediante MessageService si la validación falla
     *         o si ocurre un error en el servicio.
     */
    public manejarRegistrarUsuario(event: Event) {
        event.preventDefault();
        event.stopPropagation();
        this.limpiarErroresCamposEntrada();
        this.validarFormulario();

        if (this.isError()) {
            toastError('Error', 'Error en el formulario verifique todos los campos y intente de nuevo');
            return
        }
        
        this.alertDialogService.confirm({
            zTitle: 'Confirmar registro de usuario',
            zDescription: '¿Estás seguro de que deseas registrar al usuario?',
            zOkText: 'Continuar',
            zCancelText: 'Cancelar',
            zMaskClosable: true,
            zOnOk: () => {
                this.registrarUsuario();
            }
        });
    }

    /**
     * Funcion de registro de usuario
     */
    private registrarUsuario() {
        const nuevoUsuario = this.obtenerUsuario();
        const usuarioNormalizado = normalize(nuevoUsuario);

        this.botonRegistrar.isLoading(true);
        this.usuarioServicio.registrarUsuario(usuarioNormalizado).subscribe({
            next: res => {
                this.limpiarValoresEnCamposEntrada();
                this.botonRegistrar.isLoading(false);
                toastSuccess(
                    'Exito en el registro',
                    `el usuario ${usuarioNormalizado.nombre, usuarioNormalizado.apellido_paterno} a sido registrado con exito`
                );
            },
            error: err => {
                this.botonRegistrar.isLoading(false);
                toastError('Error de registro', err.message);
            }
        });
    }
}