package mx.edu.cbta.sistemaescolar.estructura.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "alumnos_inscritos")
public class AlumnoInscrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumno_id", nullable = false)
    private Long alumnoId;

    @Column(name="fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    @Column(name="fecha_baja")
    private LocalDate fechaBaja;
}