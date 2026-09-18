import { Component, AfterViewInit, ViewChild, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { InputSearch } from "@/shared/components/composed/inputs/inline/input-search/input-search.component";
import { TablaAlumnosComponent } from "./componentes/tabla-alumnos/tabla-alumnos.component";
import { AlumnoServicio } from "../alumno.service";
import { InformacionBasicaAlumno } from "../../../nucleo/modelos/alumnos/informacion-basica-alumno.model";
import { AdministrarAlumnoServicio } from "./administrar-alumno.service";
import { PaginatorComponent } from '@/shared/components/composed/paginator/paginator.component';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { ModalInsercionMasivaComponent } from "./componentes/modal-insercion-masiva/modal-insercion-masiva.component";
import { AppButtonComponent } from "@/shared/components/composed/button/app-button.component";
import { toastError, toastSuccess } from "@/shared/utils/toast";

@Component({
  templateUrl: './administrar-alumno.component.html',
  imports: [
    CommonModule,
    TablaAlumnosComponent,
    InputSearch,
    PaginatorComponent,
    ModalInsercionMasivaComponent,
    AppButtonComponent
  ]
})
/**
 * Componente para la administración de alumnos.
 * Permite buscar, visualizar, editar y eliminar información de los alumnos.
 */
export class AdministrarAlumnoComponente implements AfterViewInit {

  private servicioAlumnos: AlumnoServicio = inject(AlumnoServicio);
  private servicioAdministrarAlumno: AdministrarAlumnoServicio = inject(AdministrarAlumnoServicio);

  @ViewChild('studentsTable') tablaAlumnos!: TablaAlumnosComponent; 
  @ViewChild('searchInput') entradaBusqueda!: InputSearch;
  @ViewChild('paginator') paginador!: PaginatorComponent;
  @ViewChild('modalMassiveInsertion') modalMassiveInsertion!: ModalInsercionMasivaComponent;

  private readonly buscadorSubject = new Subject<string>();
  private readonly BUSQUEDA_AUTOMATICA_DELAY = 1000;
  private readonly REGISTROS_POR_PAGINA = 7;


  /**
   * Ciclo de vida del componente que se ejecuta después de que la vista del componente se ha inicializado.
   * Se utiliza para configurar la búsqueda automática y establecer los datos iniciales de la tabla de alumnos.
   */
  ngAfterViewInit(): void {
    this.configurarBusquedaAutomatica();
    this.establecerDatosIniciales();
    this.establecerItemsPorPagina();
  }

  /**
   * Establece el numero de registros que se obtendran por cada pagina en la tabla
   */
  establecerItemsPorPagina(){
    this.servicioAdministrarAlumno.establecerRegistrosPorPagina(this.REGISTROS_POR_PAGINA);
    this.paginador.setItemsPerPage(this.REGISTROS_POR_PAGINA);
  }

  /**
   * Configura la búsqueda automática de alumnos. Cada vez que el usuario deja de escribir en el campo de búsqueda
   * por un tiempo determinado, se realiza una búsqueda con el texto actual.
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
   * Se ejecuta cuando el usuario teclea en el campo de búsqueda.
   * Emite el valor actual del campo de búsqueda al Subject para activar la búsqueda automática.
   */
  alTeclear() {
    const valor = this.entradaBusqueda.getValue() ?? '';
    this.buscadorSubject.next(valor);
  }

  /**
   * Establece los datos iniciales de la tabla de alumnos, ya sea recuperándolos del servicio de administración
   * si no es la primera vez que se carga el componente, o cargando datos de prueba si es la primera vez.
   * Si hay datos previos, se restablece el estado de búsqueda y paginación.
   */
  establecerDatosIniciales() {
    // Si NO es la primera vez que entra (tiene datos guardados)
    if (!this.servicioAdministrarAlumno.esPrimeraVez()) {
      this.entradaBusqueda.setValue(this.servicioAdministrarAlumno.obtenerTextoBusqueda());
      this.tablaAlumnos.establecerAlumnosTabla(this.servicioAdministrarAlumno.obtenerAlumnos());
      this.paginador.setItemsPerPage(this.servicioAdministrarAlumno.obtenerRegistrosPorPagina());
      this.paginador.setTotalPages(this.servicioAdministrarAlumno.obtenerTotalPaginas());
      this.paginador.setPage(this.servicioAdministrarAlumno.obtenerPagina());
      return;
    }

    // Si es la primera vez, cargamos desde el servidor
    this.manejarBusquedaPorTexto();
  }

  /**
   * Confirma la eliminación de un alumno por su matrícula.
   * Muestra un mensaje flotante de error si la eliminación falla.
   * @param alumno El alumno a eliminar.
   */
  confirmarEliminacionAlumno(alumno: InformacionBasicaAlumno) {
    this.tablaAlumnos.estaCargando(true);
    const matricula = alumno.matricula;
    this.servicioAlumnos.eliminarAlumnoPorMatricula(matricula).subscribe({
      next: (data) => {
        this.tablaAlumnos.eliminarAlumnoSeleccionadoTabla();
        this.tablaAlumnos.estaCargando(false);
        toastSuccess('Éxito', 'Alumno eliminado correctamente');
      },
      error: (error) => {
        this.tablaAlumnos.estaCargando(false);
        toastError('Error al eliminar al alumno', error.message);
      }
    });
  }

  /**
   * Establece el texto de busqueda obteniendolo del input de busqueda y
   * establece el numero de pagina a 0 para reiniciar totalmente la busqueda.
   */
  manejarBusquedaPorTexto() {
    const textoBusqueda: string = this.entradaBusqueda.getValue() ?? '';
    const PAGINA: number = 0; // constant number page for a new search
    this.estadosCarga(true);
    this.buscarAlumno(textoBusqueda, PAGINA);
  }

  /**
   * Establece el texto de busqueda obteniendolo del input de busqueda y
   * establece el numero de pagina a a partir del paginador obtener una nueva pagina 
   * a partir de una misma busqueda de texto.
   */
  manejarCambioDePagina() {
    const textoBusqueda: string = this.entradaBusqueda.getValue() ?? '';
    const pagina: number = this.paginador.getPage(); //obtain the page number from the paginator component
    this.buscarAlumno(textoBusqueda, pagina);
  }

  /**
   * Realiza la búsqueda de alumnos basada en el texto de búsqueda y la página actual.
   * Actualiza la tabla de alumnos y la paginación con los resultados obtenidos.
   * Muestra un mensaje flotante de error si la búsqueda falla.
   */
  buscarAlumno(textoBusqueda: string, pagina: number) {
    this.servicioAlumnos.buscarAlumnosPorCredenciales(textoBusqueda, pagina, this.REGISTROS_POR_PAGINA).subscribe({
      next: (data) => {
        this.tablaAlumnos.establecerAlumnosTabla(data.content);
        this.servicioAdministrarAlumno.establecerAlumnos(data.content);
        this.servicioAdministrarAlumno.establecerTextoBusqueda(textoBusqueda);
        this.servicioAdministrarAlumno.establecerTotalPaginas(data.totalElements);
        this.servicioAdministrarAlumno.establecerPagina(this.paginador.getPage());
        this.servicioAdministrarAlumno.establecerPrimeraVez(false);
        this.paginador.setTotalPages(data.totalElements);
        this.paginador.setPage(pagina > 0 ? pagina : 1);
        this.estadosCarga(false);
      },
      error: (error) => {
        this.estadosCarga(false);
        toastError('Error de búsqueda', error.message);
      }
    });
  }

  /**
   * Establece el estado de carga para la tabla de alumnos.
   * @param estado `true` para activar el estado de carga, `false` para desactivarlo.
   */
  estadosCarga(estado: boolean) {
    this.tablaAlumnos.estaCargando(estado); 
    this.paginador.setDisabled(estado);
  }

  /**
   * Abre el modal de inserción masiva de alumnos.
   */
  abrirModalInsercionMasiva(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    this.modalMassiveInsertion.abrirModal();
  }

}
