package mx.edu.cbta.sistemaescolar.estructura.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ActividadParaescolarDTO {

    private Long id;

    @NotNull(message = "El nombre no puede ser null")
    @NotBlank(message = "El nombre es obligatorio")
    @JsonProperty("nombre")
    private String nombre;

    @NotNull(message = "La descripción no puede ser null")
    @NotBlank(message = "La descripción es obligatoria.")
    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("activo")
    private boolean isActivo;
}