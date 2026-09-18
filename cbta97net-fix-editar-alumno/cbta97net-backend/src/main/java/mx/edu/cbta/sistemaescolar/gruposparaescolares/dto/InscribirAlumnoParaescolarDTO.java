package mx.edu.cbta.sistemaescolar.gruposparaescolares.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull; // Recomendado
import lombok.Data;

@Data
public class InscribirAlumnoParaescolarDTO {

    @NotNull
    @JsonProperty("matricula_alumno") // El frontend debe enviar esta clave exacta
    private String matricula;

    @NotNull
    @JsonProperty("grupo_id") // El frontend debe enviar esta clave exacta
    private Long grupoId;
}