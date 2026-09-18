package mx.edu.cbta.sistemaescolar.academica.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import mx.edu.cbta.sistemaescolar.estructura.domain.model.EstadoAsistencia;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "asistencias")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumno_inscrito_id", nullable = false, updatable = false)
    private Long alumnoInscritoId;

    @Column(name = "clase_id", nullable = false)
    private Long claseId;

    @Column(name="fecha", nullable = false, updatable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name="estado", nullable = false)
    private EstadoAsistencia estado; // ASISTENCIA, FALTA, RETARDO
}