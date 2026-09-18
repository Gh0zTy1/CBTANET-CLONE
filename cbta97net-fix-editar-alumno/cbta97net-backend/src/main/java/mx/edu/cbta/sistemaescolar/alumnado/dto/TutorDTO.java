package mx.edu.cbta.sistemaescolar.alumnado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TutorDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellido_paterno")
    private String apellidoPaterno;

    @JsonProperty("apellido_materno")
    private String apellidoMaterno;

    @JsonProperty("fecha_nacimiento")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd['T'HH:mm:ss.SSS'Z']")
    private LocalDate fechaNacimiento;

    @JsonProperty("telefono")
    private String telefono;

    @JsonProperty("parentesco")
    private String parentesco;
}