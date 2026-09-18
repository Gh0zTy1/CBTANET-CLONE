import { afterNextRender, AfterViewInit, Component, inject, signal, ViewChild } from "@angular/core";
import { Location } from '@angular/common';
import { FormularioImagenComponente } from './componentes/formulario-imagen/formulario-imagen.component';
import { FormularioInfoAlumnoComponente } from './componentes/formulario-info-alumno/formulario-info-alumno.component';
import { FormularioDocumentosComponente } from './componentes/formulario-documentos/formulario-documentos.component';
import { AlumnoServicio } from "../alumno.service";
import { LoadingScreenPrimary } from "@/shared/components/composed/loading-screen/loading-screen-primary.component"
import { ActivatedRoute } from "@angular/router";
import { AlumnoInfoServicio } from "./editar-alumno-info.service";
import { Alumno } from "@modelos/alumnos/alumno.model";
import { first, firstValueFrom } from "rxjs";

@Component({
  templateUrl: './editar-alumno.component.html',
  imports: [
    FormularioInfoAlumnoComponente,
    FormularioDocumentosComponente,
    FormularioImagenComponente,
    LoadingScreenPrimary
  ]
})
export class EditarAlumnoComponente{

  @ViewChild('formularioImagen') formularioImagen!: FormularioImagenComponente;
  @ViewChild('formularioAlumnos') formularioAlumnos!: FormularioInfoAlumnoComponente;
  @ViewChild('formularioDocumentos') formularioDocumentos!: FormularioDocumentosComponente;
  @ViewChild('loadingScreen') pantallaCarga!: LoadingScreenPrimary;

  private servicioAlumnos: AlumnoServicio = inject(AlumnoServicio);
  private route: ActivatedRoute = inject(ActivatedRoute);
  private infoAlumnoServicio: AlumnoInfoServicio = inject(AlumnoInfoServicio);
  private location: Location = inject(Location);

  protected _estaCargando = signal<boolean>(false);
  protected matricula = signal<string>('');

  ngOnInit() {
    this._estaCargando.set(true);
    const m = this.route.snapshot.paramMap.get('matricula') || '';
    this.matricula.set(m);

    if (!m) {
        if (this.pantallaCarga) {
          this.pantallaCarga.isError(true);
          this.pantallaCarga.setErrorMessage('No se proporcionó una matrícula');
        }
        return;
    }
    this.infoAlumnoServicio.establecerMatricula(m);
  }

  /**
   * Constructor with an after render hook so the inputs get to render
   * after the loading screen fades away
   */
  constructor() {
      afterNextRender(() => {
          if (!this.matricula()) return;
          this.establecerDatosAlumno(this.matricula())
      });
  }

  /**
   * Funcion sincrona que devuelve al alumno cuya matricula fue 
   * especificada mediante la url de la pagina de este componente.
   */
  async establecerDatosAlumno(matricula: string): Promise<void> {
    if (!matricula) {
      throw new Error('No se proporcionó la matrícula del alumno');
    }

    this.infoAlumnoServicio.establecerMatricula(matricula);

    this.servicioAlumnos.obtenerAlumnoPorMatricula(matricula).subscribe({
      next: (alumno) => {
        this.formularioAlumnos.establecerCampos(alumno);
        this.formularioDocumentos.establecerActaNacimiento(alumno.doc_acta_nacimiento ?? "");
        this.formularioDocumentos.establecerCertificadoSecundaria(alumno.doc_certificado_secundaria ?? "");
        this.formularioDocumentos.establecerCurp(alumno.doc_curp ?? "");
        this.formularioImagen.descarImagenServidor(); // descarga la imagen desde el servidor y la establece en el componente
        this._estaCargando.set(false); // desactivar la pantalla de carga
      },
      error: (error: any) => {
        if (this.pantallaCarga) {
          this.pantallaCarga.isError(true);
          this.pantallaCarga.setErrorMessage(error?.message || 'Error al obtener la información del alumno');
        }
      }
    });
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