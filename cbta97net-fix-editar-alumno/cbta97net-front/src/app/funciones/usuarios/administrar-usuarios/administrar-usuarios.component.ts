import { Component, AfterViewInit, ViewChild, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { InputSearch } from "@/shared/components/composed/inputs/inline/input-search/input-search.component";
import { TablaUsuariosComponent } from "./componentes/tabla-usuarios/tabla-usuarios.component";
import { UsuarioServicio } from "../usuarios.service";
import { PaginatorComponent } from "@/shared/components/composed/paginator/paginator.component"; 
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AdministrarUsuarioServicio } from "./administrar-usuarios.service";
import { InformacionBasicaUsuario } from "@nucleo/modelos/usuarios/info-basica-usuario.model";
import { toastError, toastSuccess } from "@/shared/utils/toast";

@Component({
  selector: 'app-administrar-usuario',
  templateUrl: './administrar-usuarios.component.html',
  standalone: true,
  imports: [
    CommonModule,
    TablaUsuariosComponent,
    InputSearch,
    PaginatorComponent
  ]
})
/**
 * Componente para la administración de usuarios del sistema.
 * 
 * Este componente permite:
 * - Buscar usuarios por credenciales con búsqueda automática con debounce
 * - Visualizar usuarios en una tabla paginada
 * - Eliminar usuarios del sistema
 * - Mantener el estado de búsqueda y paginación entre navegaciones
 * 
 * Utiliza un servicio de estado para preservar los datos de búsqueda
 * cuando el usuario navega fuera y regresa al componente.
 */export class AdministrarUsuarioComponent implements AfterViewInit {

  private servicioUsuarios: UsuarioServicio = inject(UsuarioServicio);
  private servicioEstado: AdministrarUsuarioServicio = inject(AdministrarUsuarioServicio);

  @ViewChild('usersTable') tablaUsuarios!: TablaUsuariosComponent;
  @ViewChild('searchInput') entradaBusqueda!: InputSearch;
  @ViewChild('paginator') paginador!: PaginatorComponent;

  private readonly buscadorSubject = new Subject<string>();
  private readonly BUSQUEDA_AUTOMATICA_DELAY = 1000;
  private readonly REGISTROS_POR_PAGINA = 7;

  /**
   * Hook del ciclo de vida de Angular que se ejecuta después de que la vista se inicializa.
   * Configura la búsqueda automática y realiza la carga inicial de usuarios.
   */
  ngAfterViewInit(): void {
    this.establecerDatosIniciales();
    this.configurarBusquedaAutomatica();
    this.establecerItemsPorPagina();
  }

  /**
   * Establece el numero de registros que se obtendran por cada pagina en la tabla
   */
  establecerItemsPorPagina(){
    this.servicioEstado.establecerRegistrosPorPagina(this.REGISTROS_POR_PAGINA);
    this.paginador.setItemsPerPage(this.REGISTROS_POR_PAGINA);
  }

  /**
   * Configura la búsqueda automática con debounce para evitar múltiples llamadas al servidor.
   * Espera 1 segundo después de que el usuario deje de escribir antes de realizar la búsqueda.
   * También resetea la paginación a la primera página cuando se realiza una nueva búsqueda.
   */
  private configurarBusquedaAutomatica() {
    this.buscadorSubject.pipe(
      debounceTime(this.BUSQUEDA_AUTOMATICA_DELAY),
      distinctUntilChanged()
    ).subscribe(() => {
      this.manejarBusquedaPorTexto();
    });
  }

  /**
   * Maneja el evento de tecleo en el campo de búsqueda.
   * Obtiene el valor actual del input y lo emite al Subject para activar
   * la búsqueda automática con debounce.
   */
  alTeclear() {
    const valor = this.entradaBusqueda.getValue() ?? '';
    this.buscadorSubject.next(valor);
  }

  /**
   * Confirma y ejecuta la eliminación de un usuario del sistema.
   * Muestra estados de carga durante el proceso y notifica el resultado al usuario.
   * 
   * @param usuario - Información básica del usuario a eliminar
   */
  confirmarEliminacionUsuario(usuario: InformacionBasicaUsuario) {
    this.tablaUsuarios.estaCargando(true);
    const userId: number = Number.isNaN(usuario.id) ? 0 : Number(usuario.id);
    if (!userId) return;
    this.servicioUsuarios.eliminarUsuario(userId).subscribe({
      next: () => {
        this.tablaUsuarios.eliminarUsuarioSeleccionadoTabla();
        this.tablaUsuarios.estaCargando(false);
        toastSuccess('Éxito', 'Usuario eliminado correctamente');
      },
      error: (error) => {
        this.tablaUsuarios.estaCargando(false);
        toastError('Error al eliminar', error.message);
      }
    });
  }

  /**
   * Establece el estado de carga para los componentes de la interfaz.
   * Actualiza el estado de carga de la tabla de usuarios y del botón de búsqueda.
   * 
   * @param estado - true para mostrar estado de carga, false para ocultarlo
   */
  estadosCarga(estado: boolean) {
    this.tablaUsuarios.estaCargando(estado);
    this.paginador.setDisabled(estado);
  }

  /**
   * Establece los datos iniciales del componente.
   * Si el usuario ya había visitado este componente previamente, restaura el estado
   * guardado (texto de búsqueda, usuarios, paginación). Si es la primera vez,
   * realiza una búsqueda desde el servidor.
   */
  establecerDatosIniciales() {
    // Si NO es la primera vez que entra (tiene datos guardados)
    if (!this.servicioEstado.esPrimeraVez()) {
      this.entradaBusqueda.setValue(this.servicioEstado.obtenerTextoBusqueda());
      this.tablaUsuarios.establecerUsuariosTabla(this.servicioEstado.obtenerUsuarios());
      this.paginador.setItemsPerPage(this.servicioEstado.obtenerRegistrosPorPagina());
      this.paginador.setTotalPages(this.servicioEstado.obtenerTotalPaginas());
      this.paginador.setPage(this.servicioEstado.obtenerPagina());
      return;
    }

    // Si es la primera vez, cargamos desde el servidor
    this.manejarBusquedaPorTexto();
  }

  manejarBusquedaPorTexto() {
    const textoBusqueda: string = this.entradaBusqueda.getValue() ?? '';
    const PAGINA: number = 0 //constant number page for a new search
    this.estadosCarga(true);
    this.buscarUsuarios(textoBusqueda, PAGINA); 
  }

  manejarCambioDePagina() {
    const textoBusqueda: string = this.entradaBusqueda.getValue() ?? '';
    const pagina: number = this.paginador.getPage(); //obtain the page number from the paginator component
    this.buscarUsuarios(textoBusqueda, pagina);
  }

  /**
   * Realiza la búsqueda de usuarios por credenciales en el servidor.
   * Obtiene el texto de búsqueda y la página actual, realiza la petición al servidor,
   * actualiza la interfaz con los resultados y guarda el estado en el servicio de estado
   * para preservarlo entre navegaciones.
   */
  buscarUsuarios(textoBusqueda: string, pagina: number) {
    this.servicioUsuarios.buscarUsuariosPorCredenciales(textoBusqueda, pagina, this.REGISTROS_POR_PAGINA).subscribe({
      next: (data) => {
        console.table(data);
        // 1. Actualizar UI
        this.tablaUsuarios.establecerUsuariosTabla(data.content);
        this.paginador.setTotalPages(data.totalElements);

        // 2. Guardar en el servicio de estado
        this.servicioEstado.establecerUsuarios(data.content);
        this.servicioEstado.establecerTextoBusqueda(textoBusqueda);
        this.servicioEstado.establecerTotalPaginas(data.totalElements);
        this.servicioEstado.establecerPagina(this.paginador.getPage());
        this.servicioEstado.establecerPrimeraVez(false);

        this.estadosCarga(false);
      },
      error: (error) => {
        this.estadosCarga(false);
        toastError('Error de búsqueda', error.message);
      }
    });
  }

}