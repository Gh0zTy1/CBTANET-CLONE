package mx.edu.cbta.sistemaescolar.usuarios.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class IniciarSesionDTO {

    @NotNull(message = "El ID es obligatorio.")
    private Long id;

    @NotNull(message = "La contraseña es obligatoria.")
    //@Size(min = 8, max = 50, message = "Tamaño de contraseña no válido.")
    private String contrasena;
}
