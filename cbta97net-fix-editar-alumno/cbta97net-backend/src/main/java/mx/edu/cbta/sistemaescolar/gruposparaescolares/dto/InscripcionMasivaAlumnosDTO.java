package mx.edu.cbta.sistemaescolar.gruposparaescolares.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InscripcionMasivaAlumnosDTO {

    @NotNull(message = "El ID del grupo paraescolar es obligatorio.")
    @JsonProperty("grupo_id")
    private Long grupoId;

    @NotNull(message = "La lista de matrículas de alumnos es obligatoria.")
    @Size(min = 1, max = 100, message = "No se pueden inscribir más de 100 alumnos a un grupo.")
    @JsonProperty("matriculas")
    private List<String> matriculas;
}