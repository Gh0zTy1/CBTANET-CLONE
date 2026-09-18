package mx.edu.cbta.sistemaescolar.usuarios.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {
    private Long id;

    //@Length(min = 18, max = 18, message = "La CURP debe estar compuesta de 18 caractéres alfanuméricos.")
    //@NotNull(message = "La CURP es obligatoria")
    @JsonProperty("curp")
    private String curp;

    //@Size(max = 50, message = "El nombre del usuario es demasiado largo.")
    //@NotNull(message = "El nombre del usuario es obligatorio.")
    @JsonProperty("nombre")
    private String nombre;

    //@Size(max = 50, message = "El nombre del usuario es demasiado largo.")
    //@NotNull(message = "El apellido paterno del usuario es obligatorio.")
    @JsonProperty("apellido_paterno")
    private String apellidoPaterno;

    //@Size(max = 50, message = "El nombre del usuario es demasiado largo.")
    //@NotNull(message = "El apellido materno del usuario es obligatorio.")
    @JsonProperty("apellido_materno")
    private String apellidoMaterno;

    @Email(message = "El correo electrónico del alumno no cumple con el formato correcto.")
    //@NotNull(message = "El correo electrónico del usuario es obligatorio.")
    @JsonProperty("email")
    private String email;

    //@Size(min = 10, max = 15, message = "El número de teléfono no cumple con el formato correcto.")
    //@NotNull(message = "El número telefónico del usuario es obligatorio.")
    @JsonProperty("telefono")
    private String telefono;

    @JsonProperty("activo")
    private boolean activo;

    @JsonProperty("roles")
    private Set<RolDTO> roles;

    @JsonProperty("contrasena")
    private String contrasena;

    @JsonProperty("permisos")
    private Set<PermisoDTO> permisos;
}