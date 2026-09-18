import { Injectable, signal } from '@angular/core';
import { InformacionBasicaAlumno } from "@modelos/alumnos/informacion-basica-alumno.model";

@Injectable({ providedIn: 'root' })
export class AdministrarAlumnoServicio {

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
  private alumnos = signal<InformacionBasicaAlumno[]>([]);

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
  establecerAlumnos(usuarios: InformacionBasicaAlumno[]): void {
    this.alumnos.set(usuarios);
  }

  /**
   * Gets the stored users list
   * @returns Array of users
   */
  obtenerAlumnos(): InformacionBasicaAlumno[] {
    return this.alumnos();
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