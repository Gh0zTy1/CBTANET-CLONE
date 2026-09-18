package mx.edu.cbta.sistemaescolar.estructura.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarreraTecnicaDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("descripcion")
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 300, message = "La descripción no puede exceder los 300 caracteres")
    private String descripcion;
}