package mx.edu.cbta.sistemaescolar.horario.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clases_paraescolares")
public class ClaseParaescolar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "docente_id", nullable = false)
    private Long docenteId;

    @Column(name = "actividad_paraescolar_id", nullable = false, updatable = false)
    private Long actividadParaescolarId;

    @Column(name = "grupo_paraescolar_id", nullable = false, updatable = false)
    private Long grupoParaescolarId;

    @Column(name = "ciclo_escolar_id", nullable = false, updatable = false)
    private Long cicloEscolarId;

    //@Column(name = "aula_id", nullable = false)
    //private Long aulaId;

    @Embedded
    private Horario horario;
}