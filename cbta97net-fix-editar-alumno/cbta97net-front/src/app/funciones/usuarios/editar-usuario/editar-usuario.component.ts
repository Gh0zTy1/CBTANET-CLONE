
import { Component, inject, ViewChild, OnInit, signal, afterNextRender } from "@angular/core";
import { Location } from '@angular/common';
import { AppButtonComponent } from '@/shared/components/composed/button/app-button.component';
import { UsuarioServicio } from "@funciones/usuarios/usuarios.service";
import { InputTextOutlineComponent } from "@/shared/components/composed/inputs/outline/input-text-outline/input-text-outline.component";
import { InputCellphoneOutlineComponent } from '@/shared/components/composed/inputs/outline/input-cellphone-outline/input-cellphone-outline.component';
import { Router } from "@angular/router";
import { ActivatedRoute } from "@angular/router";
import { LoadingScreenPrimary } from "@/shared/components/composed/loading-screen/loading-screen-primary.component"
import { InputPasswordOutlineComponent } from "@/shared/components/composed/inputs/outline/input-password-outline/input-password-outline.component";
import { ZardDividerComponent } from '@/shared/components/base/divider/divider.component';
import { AlertComposedComponent } from '@/shared/components/base/alert/alert.component';
import { ZardAlertDialogService } from '@shared/components/base/alert-dialog/alert-dialog.service';
import { toastError, toastSuccess } from "@/shared/utils/toast";
import { UsuarioGuardar } from "@/nucleo/modelos/usuarios/usuario-guardar.model";
import { normalize } from "@/shared/utils/object-sanitizer";

@Component({
    selector: 'app-editar-usuario',
    templateUrl: './editar-usuario.component.html',
    standalone: true,
    imports: [
        AppButtonComponent,
        InputTextOutlineComponent,
        InputCellphoneOutlineComponent,
        LoadingScreenPrimary,
        InputPasswordOutlineComponent,
        ZardDividerComponent,
        AlertComposedComponent,
    ]
})
export class EditarUsuarioComponente implements OnInit {
    // Inyecciones
    private readonly route = inject(ActivatedRoute);
    private readonly router = inject(Router);
    private readonly usuarioServicio = inject(UsuarioServicio);
    private location: Location = inject(Location);
    private alertDialogService = inject(ZardAlertDialogService);  

    // Parámetro de la URL
    idUrl: number | null = null;

    //public esActivo = signal<boolean>(true);
    protected _estaCargando = signal<boolean>(true);
    //global form error flag
    private isError = signal<boolean>(false);

    @ViewChild('loadingScreen') pantallaCarga!: LoadingScreenPrimary;
    @ViewChild('nombre') protected nombreInput!: InputTextOutlineComponent;
    @ViewChild('apellidoPaterno') apellidoPaternoInput!: InputTextOutlineComponent;
    @ViewChild('apellidoMaterno') apellidoMaternoInput!: InputTextOutlineComponent;
    @ViewChild('curp') curpInput!: InputTextOutlineComponent;
    @ViewChild('telefono') telefonoInput!: InputCellphoneOutlineComponent;
    @ViewChild('email') emailInput!: InputTextOutlineComponent;
    @ViewChild('botonActualizar') botonGuardar!: AppButtonComponent;
    @ViewChild('contrasena') contrasenaInput!: InputPasswordOutlineComponent;
    @ViewChild('confirmarContrasena') confirmarContrasenaInput!: InputPasswordOutlineComponent;

    ngOnInit() {
        const idStr = this.route.snapshot.paramMap.get('id') || "0";
        this.idUrl = Number.parseInt(idStr);

        if (!this.idUrl) {
            this.pantallaCarga.isError(true);
            this.pantallaCarga.setErrorMessage('No se proporcionó una matrícula');
            return
        }
    }

    /**
     * Constructor with an after render hook so the inputs get to render
     * after the loading screen fades away
     */
    constructor() {
        afterNextRender(() => {
            if (!this.idUrl) return;
            this.cargarDatosUsuario(this.idUrl)
        });
    }

    /**
     * Encargado de la inicializacion de los datos del usuario a la entrada
     * de la pantalla.
     * @param id id del usuario a consultar y establecer.
     */
    private cargarDatosUsuario(id: number) {
        this.usuarioServicio.obtenerUsuarioPorId(id).subscribe({
            next: (usuario) => {
                setTimeout(() => {
                    this._estaCargando.set(false); //deactivate the loading screen
                    this.nombreInput.setValue(usuario.nombre);
                    this.apellidoPaternoInput.setValue(usuario.apellido_paterno);
                    this.apellidoMaternoInput.setValue(usuario.apellido_materno);
                    this.curpInput.setValue(usuario.curp);
                    this.emailInput.setValue(usuario.email);
                    this.telefonoInput.setValue(usuario.telefono);
                }, 50);
            },
            error: (err) => {
                this.pantallaCarga.isError(true);
                this.pantallaCarga.setErrorMessage(err.message);
            }
        });
    }

    /**
     * Funcion encargada de la orquestacion de la actualizacion del usuario en servidor.
     * 
     * Esta función coordina todo el flujo de actualización:
     * 1. Previene el comportamiento por defecto del evento
     * 2. Limpia errores previos y resetea el estado de error
     * 3. Valida todos los campos del formulario
     * 4. Si la validación es exitosa, actualiza la información del usuario
     * 5. Muestra mensajes de éxito o error según corresponda
     * 
     * @param {Event} event - Evento que dispara la actualización (generalmente un submit)
     */
    public manejarActualizacionDeUsuario(event: Event) {
        event.preventDefault();
        event.stopPropagation();
        this.limpiarErroresEnCammposEntrada();
        this.validarFormulario();

        if (this.isError()) {
            toastError('Error', 'Error en campos de formulario, complete todos he intente de nuevo');
            return
        }

        this.alertDialogService.confirm({
            zTitle: 'Confirmar actualización de usuario',
            zDescription: '¿Estás seguro de que deseas actualizar al usuario?',
            zOkText: 'Continuar',
            zCancelText: 'Cancelar',
            zMaskClosable: true,
            zOnOk: () => {
                this.actualizarUsuario();
            }
        });
    }

    /**
     * Actualiza el usuario en el servidor.
     */
    private actualizarUsuario(): void {
        const usuarioActualizado = this.obtenerUsuarioParaActualizar();
        console.log(usuarioActualizado);

        this.botonGuardar.isLoading(true);
        if(!usuarioActualizado) return
        this.usuarioServicio.modificarUsuario(this.idUrl!, usuarioActualizado).subscribe({
            next: (res) => {
                console.log(res);
                toastSuccess('Usuario actualizado', res);
                this.botonGuardar.isLoading(false);
                this.contrasenaInput.hideError();
                this.confirmarContrasenaInput.hideError();
            },
            error: (err) => {
                toastError('Error', err.message);
                this.botonGuardar.isLoading(false);
            }
        });
    }

    /**
     * Método auxiliar para extraer datos de los inputs.
     * @returns Objeto de tipo ModificarUsuario.
     */
    private obtenerUsuarioParaActualizar(): UsuarioGuardar {
        const usuario: UsuarioGuardar = {
            nombre: this.nombreInput.getValue(),
            apellido_paterno: this.apellidoPaternoInput.getValue(),
            apellido_materno: this.apellidoMaternoInput.getValue(),
            curp: this.curpInput.getValue(),
            telefono: this.telefonoInput.getValue(),
            email: this.emailInput.getValue(),
            activo: true,
            contrasena: this.contrasenaInput.getValue()
        }
        return normalize(usuario); //normalize before sending
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

        this.verificarCamposContraseña();
    }

    /**
     * Genera y aplica las validaciones para los diferentes flujos en inputs de contraseñas.
     */
    verificarCamposContraseña() {
        const contrasena = this.contrasenaInput.getValue() ?? '';
        const confirmarContrasena = this.confirmarContrasenaInput.getValue() ?? '';

        if (contrasena.length < 8) {
            const message = 'La contraseña debe tener al menos 8 caracteres.';
            this.contrasenaInput.showError(message);
            this.isError.set(true);
            return
        }

        if (contrasena !== confirmarContrasena) {
            const message = 'Las contraseñas no coinciden.';
            this.contrasenaInput.showError(message);
            this.confirmarContrasenaInput.showError(message);
            this.isError.set(true);
            return
        }
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
     * Verifica que el numero de telefono solo contenga 10 numeros consecutivos.
     * @param telefono Número telefónico del usuario.-
     */
    private validarTelefono(telefono: string) {
        const regex = /^[0-9]{6,10}$/;
        return regex.test(telefono.trim());
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
     * Verifica que el texto ingresado corresponda al formato de un correo electrónico.
     * @param email Correo electrónico del usuario.
     * @returns true si es un correo electrónico válido.
     */
    private validarEmail(email: string) {
        const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        return regex.test(email);
    }

    /**
     * Limpia todos los mensajes de error visuales de los campos del formulario.
     * Oculta los errores mostrados en cada campo de entrada y restablece
     * la bandera de error global (isError) a false.
     * Se ejecuta antes de realizar una nueva validación para evitar mostrar errores previos.
     */
    limpiarErroresEnCammposEntrada() {
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
    * Funcion auxiliar emitida por el componente encargada de ejecutar logica de
    * retorno a pantalla anterior en caso de fallo en la carga de la informacion 
    * del alumno.
    */
    manejarRetornoEnError(event: Event) {
        event.preventDefault();
        event.stopPropagation();
        this.location.back();
    }


}