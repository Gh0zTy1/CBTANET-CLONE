import { Injectable, signal } from '@angular/core';
import { InformacionBasicaUsuario } from '@modelos/usuarios/info-basica-usuario.model';

/**
 * Service to manage user admin state.
 * Preserves search state, pagination, and user list between navigations.
 * 
 * @example
 * ```typescript
 * constructor(private servicio: AdministrarUsuarioServicio) {}
 * 
 * ngOnInit() {
 *   if (!this.servicio.esPrimeraVez()) {
 *     this.entradaBusqueda.setValue(this.servicio.obtenerTextoBusqueda());
 *   }
 * }
 * ```
 */
@Injectable({ providedIn: 'root' })
export class AdministrarUsuarioServicio {

  /** Signal for first visit tracking */
  private primeraVez = signal<boolean>(true);
  /** Signal for search text */
  private textoBusqueda = signal<string>('');
  /** Signal for current page number */
  private pagina = signal<number>(0);
  /** Signal for total pages */
  private totalPaginas = signal<number>(0);

  private registrosPorPagina = signal<number>(0);

  /** Signal for user list */
  private usuarios = signal<InformacionBasicaUsuario[]>([]);

  /**
   * Checks if this is the first visit
   * @returns true if first visit
   */
  esPrimeraVez(): boolean {
    return this.primeraVez();
  }

  /**
   * Sets the first visit state
   * @param estado - Whether this is the first visit
   */
  establecerPrimeraVez(estado: boolean): void {
    this.primeraVez.set(estado);
  }

  /**
   * Sets the users list
   * @param usuarios - Array of users to store
   */
  establecerUsuarios(usuarios: InformacionBasicaUsuario[]): void {
    this.usuarios.set(usuarios);
  }

  /**
   * Gets the stored users list
   * @returns Array of users
   */
  obtenerUsuarios(): InformacionBasicaUsuario[] {
    return this.usuarios();
  }

  /**
   * Sets the current page number
   * @param pagina - Page number
   */
  establecerPagina(pagina: number): void {
    this.pagina.set(pagina);
  }

  /**
   * Gets the current page number
   * @returns Page number
   */
  obtenerPagina(): number {
    return this.pagina();
  }

  /**
   * Sets the total pages
   * @param total - Total number of pages
   */
  establecerTotalPaginas(total: number): void {
    this.totalPaginas.set(total);
  }

  /**
   * Gets the total pages
   * @returns Total pages
   */
  obtenerTotalPaginas(): number {
    return this.totalPaginas();
  }

  /**
   * Establece el total de registros por pagina a almacenar
   * @param total - Numero total de registros
   */
  establecerRegistrosPorPagina(numRegistros: number): void {
    this.registrosPorPagina.set(numRegistros);
  }

  /**
   * Regresa el total de registros por pagina a almacenados
   * @returns Numero total de registros
   */
  obtenerRegistrosPorPagina(): number {
    return this.registrosPorPagina();
  }

  /**
   * Sets the search text
   * @param texto - Search text
   */
  establecerTextoBusqueda(texto: string): void {
    this.textoBusqueda.set(texto);
  }

  /**
   * Gets the search text
   * @returns Search text
   */
  obtenerTextoBusqueda(): string {
    return this.textoBusqueda();
  }
}