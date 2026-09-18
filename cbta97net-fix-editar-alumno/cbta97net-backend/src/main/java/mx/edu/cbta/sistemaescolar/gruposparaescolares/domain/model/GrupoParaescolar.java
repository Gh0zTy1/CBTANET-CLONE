package mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa un grupo destinado a actividades paraescolares (deportivas, culturales, etc.).
 * Esta entidad gestiona la disponibilidad de espacios y la asignación de un docente específico
 * para una actividad dentro de un ciclo escolar determinado.
 */
@Getter
@Setter
@Entity
@Table(name = "grupos_paraescolares")
public class GrupoParaescolar {

    /**
     * Identificador único de la inscripción al grupo paraescolar.
     * Generado automáticamente mediante una estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Texto breve utilizado para distinguir o describir el grupo.
     * Mapeado a la columna 'nota' con un límite de 255 caracteres.
     */
    @Column(name = "nota", length = 255)
    private String notaIdentificatoria;

    /**
     * Capacidad máxima de alumnos permitidos en este grupo paraescolar.
     * Se utiliza para el control de inscripciones y evitar sobrecupos.
     */
    @Column(name = "maximo_espacios")
    private Integer maximoEspacios;

    /**
     * Identificador del ciclo escolar al que pertenece el grupo.
     * Este campo es obligatorio para situar la actividad en un periodo de tiempo.
     */
    @Column(name = "ciclo_escolar_id", nullable = false)
    private Long cicloEscolarId;

    /**
     * Identificador de la actividad paraescolar específica (ej. Fútbol, Danza, Música).
     * Referencia obligatoria a la configuración de actividades.
     */
    @Column(name = "actividad_paraescolar_id", nullable = false)
    private Long actividadParaescolarId;

    /**
     * Identificador del docente responsable de impartir la actividad paraescolar.
     * No se permiten grupos sin un docente asignado.
     */
    @Column(name = "docente_id", nullable = false)
    private Long docenteId;
}