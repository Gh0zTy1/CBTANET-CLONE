package mx.edu.cbta.sistemaescolar.estructura.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.HorasPorSemanaException;

@Getter
@Setter
@Entity
@Table(name = "materias")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "horas_por_semana", nullable = false)
    private int horasPorSemana;

    @Column(name = "grado", nullable = false)
    @Min(value = 1, message = "El semestre debe ser como mínimo 1 (Primer Semestre)")
    @Max(value = 6, message = "El semestre no puede ser mayor a 6 (Sexto Semestre)")
    private int semestre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_tecnica_id", insertable = false, updatable = false)
    private CarreraTecnica carreraTecnica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_propedeutica_id", insertable = false, updatable = false)
    private AreaPropedeutica areaPropedeutica;

    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Size(min = 1, message = "Una materia debe tener al menos una unidad.")
    private Set<Unidad> unidades;

    /**
     * Verifica si los minutos asignados para la materia en cuestion cumplen con las
     * horas por semana necesarias para la materia durante el semestre.
     * @param totalMinutosAsignados Cantidad de minutos asignados a la materia.
     * @return
     */
    public boolean cumpleConHorasPorSemana(long totalMinutosAsignados) throws HorasPorSemanaException {

        long minutosRequeridosPorMateria = this.horasPorSemana * 60L;

        double horasAsignadas = totalMinutosAsignados / 60.0;

        if (totalMinutosAsignados < minutosRequeridosPorMateria) {
            throw new HorasPorSemanaException(
                    String.format(
                            "Las horas asignadas (%.2f) son menos que las horas por semana requeridas para la materia (%d).",
                            horasAsignadas,
                            this.getHorasPorSemana()
                    )
            );
        }

        if (totalMinutosAsignados > minutosRequeridosPorMateria) {
            throw new HorasPorSemanaException(
                    String.format(
                            "Las horas asignadas (%.2f) son más que las horas por semana requeridas para la materia (%d).",
                            horasAsignadas,
                            this.getHorasPorSemana()
                    )
            );
        }

        return true;
    }
}