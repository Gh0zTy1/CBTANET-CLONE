import { Injectable, signal } from '@angular/core';

@Injectable({providedIn: 'root'})
export class AlumnoInfoServicio {

    private matriculaAlumno = signal<string>('');

    establecerMatricula(matricula: string): void {
        this.matriculaAlumno.set(matricula)
    }

    obtenerMatricula(): string {
        return this.matriculaAlumno();
    }
}