package mx.edu.cbta.sistemaescolar.horario.domain.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clases")
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "docente_id", nullable = false)
    private Long docenteId;

    @Column(name = "materia_id", nullable = false, updatable = false)
    private Long materiaId;

    @Column(name = "grupo_id", nullable = false, updatable = false)
    private Long grupoId;

    @Column(name = "ciclo_escolar_id", nullable = false, updatable = false)
    private Long cicloEscolarId;

    @Column(name = "aula_id", nullable = false)
    private Long aulaId;

    @Embedded
    private Horario horario;
}