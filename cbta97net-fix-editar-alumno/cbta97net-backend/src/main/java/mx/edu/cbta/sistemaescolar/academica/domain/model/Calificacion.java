package mx.edu.cbta.sistemaescolar.academica.domain.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "calificaciones")
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_inscrito_id", nullable = false)
    private AlumnoInscrito alumnoInscrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;

    @Column(name="puntaje", nullable = false)
    private Double puntaje;

     */
}