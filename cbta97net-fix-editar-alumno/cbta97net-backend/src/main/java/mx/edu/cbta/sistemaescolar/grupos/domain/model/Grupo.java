package mx.edu.cbta.sistemaescolar.grupos.domain.model;

import mx.edu.cbta.sistemaescolar.estructura.domain.model.Turno;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "grupos_semestrales")
public class Grupo {

    /**
     * Identificador único autoincremental del grupo.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Observaciones o notas adicionales del grupo.
     * Limitado a 300 caracteres. No permite nulos.
     */
    @Column(name="nota", length = 300)
    private String nota;

    /**
     * Carácter que identifica al grupo (Ej: 'A', 'B', 'C').
     * No permite nulos.
     */
    @Column(name="letra", nullable = false)
    private Character letra;

    /**
     * Número que representa el semestre actual (1 al 6).
     * Nota: Posee una discrepancia técnica al usar @Enumerated sobre un tipo int.
     */
    @Column(name="semestre", nullable = false)
    private int semestre;

    /**
     * Turno asignado al grupo (MATUTINO o VESPERTINO).
     * Se almacena como String en la base de datos.
     */
    @Enumerated(EnumType.STRING)
    @Column(name="turno", nullable = false)
    private Turno turno;

    /**
     * ID de referencia al ciclo escolar.
     * Es obligatorio y no puede ser modificado después de la creación.
     */
    @Column(name = "ciclo_escolar_id", nullable = false, updatable = false)
    private Long cicloEscolarId;

    /**
     * ID de referencia a la carrera técnica asociada.
     * Campo opcional (puede ser nulo).
     */
    @Column(name = "carrera_tecnica_id")
    private Long carreraTecnicaId;

    /**
     * ID de referencia al área propedéutica (usualmente para semestres superiores).
     * Campo opcional (puede ser nulo).
     */
    @Column(name = "area_propedeutica_id")
    private Long areaPropedeuticaId;

    public static String obtenerNombreSemestre(int semestre) {
        return switch (semestre) {
            case 1 -> "Primer semestre";
            case 2 -> "Segundo semestre";
            case 3 -> "Tercer semestre";
            case 4 -> "Cuarto semestre";
            case 5 -> "Quinto semestre";
            case 6 -> "Sexto semestre";
            default -> "Semestre inválido";
        };
    }

    public String obtenerNombreSemestre() {
        return switch (this.semestre) {
            case 1 -> "Primer semestre";
            case 2 -> "Segundo semestre";
            case 3 -> "Tercer semestre";
            case 4 -> "Cuarto semestre";
            case 5 -> "Quinto semestre";
            case 6 -> "Sexto semestre";
            default -> "Semestre inválido";
        };
    }
}