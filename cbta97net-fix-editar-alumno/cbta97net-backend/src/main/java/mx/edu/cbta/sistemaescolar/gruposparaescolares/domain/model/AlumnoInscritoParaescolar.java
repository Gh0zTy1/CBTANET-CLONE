package mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "alumnos_inscritos_paraescolar")
public class AlumnoInscritoParaescolar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumno_id", nullable = false)
    private Long alumnoId;

    @Column(name = "grupo_paraescolar_id", nullable = false, updatable = false)
    private Long grupoParaescolarId;

    @Column(name="fecha_inscripcion", nullable = false, updatable = false)
    private LocalDate fechaInscripcion;

    @Column(name="fecha_baja")
    private LocalDate fechaBaja;
}
