package mx.edu.cbta.sistemaescolar.personal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocenteDTO {
    // Campos de Usuario

    @JsonProperty("id_usuario")
    private Long usuarioId;

    @JsonProperty("curp")
    private String curp;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellido_paterno")
    private String apellidoPaterno;

    @JsonProperty("apellido_materno")
    private String apellidoMaterno;

    @JsonProperty("email")
    private String email;

    @JsonProperty("telefono")
    private String telefono;

    @JsonProperty("activo")
    private boolean activo;

    @JsonProperty("roles")
    private Set<String> roles;

    @JsonProperty("id_docente")
    private Long idDocente;

    // Campos de Docente
    @JsonProperty("cedula_profesional")
    private String cedulaProfesional;

    // Relaciones aplanadas (en lugar de objetos completos)
    @JsonProperty("clases")
    private Set<Long> claseIds;

    @JsonProperty("materias_calificadas")
    private Set<String> materiaNombres;
}