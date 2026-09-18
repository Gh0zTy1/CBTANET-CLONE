package mx.edu.cbta.sistemaescolar.usuarios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AsignarRolesDTO {
    @NotNull(message = "La lista de roles es obligatoria")
    private List<String> roles;
}
