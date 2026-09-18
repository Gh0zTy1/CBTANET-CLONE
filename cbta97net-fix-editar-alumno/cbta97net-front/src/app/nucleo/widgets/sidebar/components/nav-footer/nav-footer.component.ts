import { afterNextRender, Component, inject, OnInit, Output, signal } from '@angular/core';
import { ZardAlertDialogService } from '@shared/components/base/alert-dialog/alert-dialog.service';
import { CurrentUserService } from '@/nucleo/servicios/current-user.service';
import { AuthService } from '@/nucleo/servicios/auth.service';
import { toastError } from '@/shared/utils/toast';

@Component({
    selector: 'sidebar-footer',
    templateUrl: './nav-footer.component.html'
})
export class SidebarFooterComponent {
    userId = signal<number>(0);
    roles = signal<string[]>([]);

    private alertDialogService = inject(ZardAlertDialogService);
    private currentUserService = inject(CurrentUserService);
    private authService = inject(AuthService);
    
    constructor () {
        afterNextRender(()=>{
            this.userId.set(this.currentUserService.getUserId());
    
            if (!this.currentUserService.getRoles() || this.currentUserService.getRoles().length === 0) return // return if empty
            const normalizedRoles = this.currentUserService.getRoles().map(role => {
                return role.charAt(0).toUpperCase() + role.slice(1).toLowerCase()
            });
            this.roles.set(normalizedRoles);
        }) 
    }

    cerrarSesion() {
        this.authService.cerrarSesion().subscribe({
            error: () => {
                toastError(
                    'Error de cierre de sesion',
                    'error inesperado de cierre de sesion, intente mas tarde'
                )
            }
          });
    }
    
    handleLogOut(event: Event) {
        event.preventDefault();
        event.stopPropagation();
        console.log('entrando a cerrar sesion')
        this.alertDialogService.confirm({
            zTitle: 'Confirmar cierre de sesión',
            zDescription:
            '¿Estás seguro de que deseas cerrar la sesión?, si tiene cambios sin guardar podrian no recuperarse',
            zOkText: 'Continuar',
            zCancelText: 'Cancelar',
            zMaskClosable: true,
            zOnOk: (()=> {
                this.cerrarSesion();
            })
        });
    }
}

 