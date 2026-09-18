package mx.edu.cbta.sistemaescolar.alumnado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
// Importamos los DTOs de los otros paquetes que sí reutilizamos
import mx.edu.cbta.sistemaescolar.personal.dto.DocenteDTO;

import java.time.LocalDate;
import java.util.Set;

@Data
public class SolicitarAlumnoDetalleDTO {

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
    private LocalDate fechaNacimiento;

    @JsonProperty("nss")
    private String numeroSeguroSocial;

    @JsonProperty("poliza_seguro")
    private String numeroPolizaSeguro;

    @JsonProperty("tutor_legal")
    private TutorDTO tutorLegal;

    @JsonProperty("condicion_especial_desc")
    private String condicionEspecialDescripcion;
}