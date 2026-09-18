package mx.edu.cbta.sistemaescolar.estructura.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.Data;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.HorasPorSemanaException;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MateriaDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("semestre")
    private int semestre;

    @JsonProperty("horas_por_semana")
    @Min(value = 1, message = "La materia debe contar con al menos 1 hora de estudio por semana.")
    private int horasPorSemana;

    // Aplanamos las relaciones
    @JsonProperty("carrera_tecnica_id")
    private Long carreraTecnicaId;

    @JsonProperty("carrera_tecnica_nombre")
    private String carreraTecnicaNombre;

    @JsonProperty("area_propedeutica_id")
    private Long areaPropedeuticaId;

    @JsonProperty("area_propedeutica_nombre")
    private String areaPropedeuticaNombre;

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
                            "Las horas asignadas (%.2f) son menos que las horas por semana requeridas para la materia '%s' (%d).",
                            horasAsignadas,
                            this.getNombre(),
                            this.getHorasPorSemana()
                    )
            );
        }

        if (totalMinutosAsignados > minutosRequeridosPorMateria) {
            throw new HorasPorSemanaException(
                    String.format(
                            "Las horas asignadas (%.2f) son más que las horas por semana requeridas para la materia '%s' (%d).",
                            horasAsignadas,
                            this.getNombre(),
                            this.getHorasPorSemana()
                    )
            );
        }

        return true;
    }
}