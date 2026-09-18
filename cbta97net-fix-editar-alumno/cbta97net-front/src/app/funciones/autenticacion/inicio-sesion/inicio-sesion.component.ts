import { Router } from "@angular/router"
import { Component, inject, signal, ViewChild } from "@angular/core";
import { AppButtonComponent } from '@/shared/components/composed/button/app-button.component';
import { InputNumberIconComponent } from '@/shared/components/composed/inputs/inline/input-number-icon/input-number-icon.component';
import { InputPasswordIconComponent } from '@/shared/components/composed/inputs/inline/input-password-icon/input-password-icon.component';
import { AuthService } from "@nucleo/servicios/auth.service";
import { AlertComposedComponent } from '@/shared/components/base/alert/alert.component';
import { ZardDividerComponent } from '@/shared/components/base/divider/divider.component';
import { CurrentUserService } from '@/nucleo/servicios/current-user.service';


@Component({
  templateUrl: './inicio-sesion.component.html',
  imports: [
    AppButtonComponent,
    InputNumberIconComponent,
    InputPasswordIconComponent,
    AlertComposedComponent,
    ZardDividerComponent
  ]
})
export class InicioSesionComponente {

  private router: Router = inject(Router);
  private authService: AuthService = inject(AuthService);

  @ViewChild('IdInput') private id!: InputNumberIconComponent;
  @ViewChild('passwordInput') private contrasena!: InputPasswordIconComponent;
  @ViewChild('loginButton') private botnIniciarSesion!: AppButtonComponent;

  protected errorMessage = signal<string>('');
  protected isErrorMessageShowing = signal(false);

  private currentUserService = inject(CurrentUserService);

  /**
   * Valida los datos de entrada (matricula y contraseña) y verifica
   * que cumplan con requisitos especificos antes de validar en servidor.
   */
  private validarDatos = () => {
    if (!this.id.getValue() && !this.contrasena.getValue()) {
      this.id.showError('');
      this.contrasena.showError('');
      this.isErrorMessageShowing.set(true);
      throw new Error('Complete todos los campos antes de continuar');
    }
    if (!this.id.getValue()) {
      this.id.showError('');
      this.isErrorMessageShowing.set(true);
      throw new Error('Complete todos los campos antes de continuar');
    }
    if (!this.contrasena.getValue()) {
      this.contrasena.showError('');
      this.isErrorMessageShowing.set(true);
      throw new Error('Complete todos los campos antes de continuar');
    }
  };

  /**
   * Reestablece los valores de errores de los campos de entrada en 
   * default para que vuelvan a los estilos predeterminados.
   */
  private resetearValoresError = () => {
    this.id.hideError()
    this.contrasena.hideError();
    this.isErrorMessageShowing.set(false);
  };

  /**
   * Funcion axiliar que permite la navegacion a la plantilla de dashboard.
   */
  private navegarAlDashboard = () => {
    this.router.navigate(['app'], { replaceUrl: true });
  }

  /**
   * Funcion auxiliar para guardar al usuario logueado para uso global de la 
   * aplicacion.
   */
  private guardarUsuarioGlobalmente = () => {
    throw Error('Not implemented yet');
  }

  activarEstadoCarga(): void {
    this.botnIniciarSesion.isLoading(true);
  }

  desactivarEstadoCarga(): void {
    this.botnIniciarSesion.isLoading(false);
  }

  /**
   * Manejo principal del inicio de sesion.
   * @param event Evento que desencadeno la funcion.
   */
  handleLogin(event: Event) {
    try {
      console.log("si me presionaron");
      event.preventDefault();
      this.resetearValoresError();
      this.validarDatos();
      this.activarEstadoCarga();

      const id = this.id.getValue();
      const contrasena = this.contrasena.getValue();
      //this.navegarAlDashboard();

      this.authService.iniciarSesion(id!, contrasena).subscribe({
        next: (res) => {
          this.currentUserService.setId(Number(id));
          console.log('Éxito:', res);
          this.navegarAlDashboard();
        },
        // TODO: en caso de error no me debe dejar pasar al Dashboard
        error: (msgServidor) => {
          this.desactivarEstadoCarga();
          console.error(msgServidor);
          this.isErrorMessageShowing.set(true);
          this.errorMessage.set(msgServidor);
        }
      });

    } catch (error) {
      if (error instanceof Error) {
        this.errorMessage.set(error.message);
      }
    }
  }
}