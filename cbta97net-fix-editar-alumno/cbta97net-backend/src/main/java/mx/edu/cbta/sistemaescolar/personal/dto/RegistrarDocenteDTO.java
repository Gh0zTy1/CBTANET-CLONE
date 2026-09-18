package mx.edu.cbta.sistemaescolar.personal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
public class RegistrarDocenteDTO {

    @NotNull(message = "La cédula profesional es obligatoria.")
    @Pattern(
            regexp = "^\\d{7,8}$",
            message = "La cédula profesional debe contener 7 u 8 dígitos numéricos."
    )
    @JsonProperty("cedula_profesional")
    private String cedulaProfesional;


    @NotNull(message = "El ID de usuario del docente es obligatorio.")
    @JsonProperty("id_usuario")
    private Long usuarioId;
}
