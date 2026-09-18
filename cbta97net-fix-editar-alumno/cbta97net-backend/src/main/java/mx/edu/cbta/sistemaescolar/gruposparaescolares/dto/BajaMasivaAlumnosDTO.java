package mx.edu.cbta.sistemaescolar.gruposparaescolares.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BajaMasivaAlumnosDTO {

    @NotNull(message = "El ID del grupo paraescolar es obligatorio.")
    @JsonProperty("grupo_id")
    private Long grupoId;

    @NotNull(message = "La lista de matrículas de alumnos es obligatoria.")
    @Size(min = 1, max = 100, message = "No se puede dar de baja más de 100 alumnos en un grupo.")
    @JsonProperty("matriculas")
    private List<String> matriculas;
}