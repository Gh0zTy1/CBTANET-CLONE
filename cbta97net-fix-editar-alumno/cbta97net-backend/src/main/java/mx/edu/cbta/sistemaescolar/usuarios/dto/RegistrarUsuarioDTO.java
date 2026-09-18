package mx.edu.cbta.sistemaescolar.usuarios.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrarUsuarioDTO {

    @JsonProperty("curp")
    @NotBlank(message = "La CURP es obligatoria.")
    @Pattern(
            regexp = "^[A-Z0-9]{18}$",
            message = "La CURP debe contener exactamente 18 caracteres alfanuméricos en mayúsculas."
    )
    private String curp;

    @JsonProperty("nombre")
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres.")
    /*
    @Pattern(
            regexp = "^[A-Z ]{1,50}$",
            message = "El nombre solo puede contener letras mayúsculas sin acentos."
    )*/
    private String nombre;

    @JsonProperty("apellido_paterno")
    @NotBlank(message = "El apellido paterno es obligatorio.")
    @Size(max = 50, message = "El apellido paterno no puede superar los 50 caracteres.")
    /*
    @Pattern(
            regexp = "^[A-Z ]{1,50}$",
            message = "El apellido paterno solo puede contener letras mayúsculas sin acentos."
    )*/
    private String apellidoPaterno;

    @JsonProperty("apellido_materno")
    @NotBlank(message = "El apellido materno es obligatorio.")
    @Size(max = 50, message = "El apellido materno no puede superar los 50 caracteres.")
    /*
    @Pattern(
            regexp = "^[A-Z ]{1,50}$",
            message = "El apellido materno solo puede contener letras mayúsculas sin acentos."
    )*/
    private String apellidoMaterno;

    @JsonProperty("email")
    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Size(max = 70, message = "El email no puede superar los 70 caracteres.")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "El email no tiene un formato válido."
    )
    private String email;

    @JsonProperty("telefono")
    @NotBlank(message = "El teléfono es obligatorio.")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "El teléfono debe contener exactamente 10 dígitos."
    )
    private String telefono;

    @JsonProperty("contrasena")
    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(max = 30, message = "La contraseña no puede superar los 30 caracteres.")
    private String contrasena;

    @JsonProperty("is_activo")
    private boolean activo = true;
}