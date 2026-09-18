package mx.edu.cbta.sistemaescolar.usuarios.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RolDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("tipo")
    private final String tipo = "rol";

    public RolDTO(String id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
