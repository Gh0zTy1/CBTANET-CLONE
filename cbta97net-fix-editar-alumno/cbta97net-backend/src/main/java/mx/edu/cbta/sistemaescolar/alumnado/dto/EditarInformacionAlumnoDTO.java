package mx.edu.cbta.sistemaescolar.alumnado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EditarInformacionAlumnoDTO {

    @JsonProperty("matricula")
    private String matricula;

    @JsonProperty("curp")
    private String curp;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellido_paterno")
    private String apellidoPaterno;

    @JsonProperty("apellido_materno")
    private String apellidoMaterno;

    @JsonProperty("fecha_nacimiento")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd['T'HH:mm:ss.SSS'Z']")
    private LocalDate fechaNacimiento;

    @JsonProperty("nss")
    private String numeroSeguroSocial;

    @JsonProperty("poliza_seguro")
    private String numeroPolizaSeguro;

    @JsonProperty("tutor_legal")
    private TutorDTO tutorLegal;

    @JsonProperty("direccion")
    private DireccionDTO direccion;

    @JsonProperty("condicion_especial_desc")
    private String condicionEspecialDescripcion;
}

