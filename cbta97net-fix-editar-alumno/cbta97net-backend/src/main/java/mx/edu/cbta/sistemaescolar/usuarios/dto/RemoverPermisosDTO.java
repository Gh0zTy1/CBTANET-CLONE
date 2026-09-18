package mx.edu.cbta.sistemaescolar.usuarios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RemoverPermisosDTO {
    @NotNull(message = "La lista de permisos es obligatoria")
    private List<String> permisos;
}
