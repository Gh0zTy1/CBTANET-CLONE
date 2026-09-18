package mx.edu.cbta.sistemaescolar.gruposparaescolares.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlumnoInscritoParaescolarDTO {

    @JsonProperty("alumno_id")
    private Long alumnoId;

    @JsonProperty("matricula")
    private String matricula;

    @JsonProperty("nombre")
    private String nombreCompleto;

    @JsonProperty("grupo_paraescolar_id")
    private Long grupoParaescolarId;

    @JsonProperty("fecha_inscripcion")
    private LocalDate fechaInscripcion;

    @JsonProperty("fecha_baja")
    private LocalDate fechaBaja;
}