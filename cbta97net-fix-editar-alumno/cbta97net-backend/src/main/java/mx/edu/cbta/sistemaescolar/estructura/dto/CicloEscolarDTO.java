package mx.edu.cbta.sistemaescolar.estructura.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CicloEscolarDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("fecha_inicio")
    private LocalDate fechaInicio;

    @JsonProperty("fecha_fin")
    private LocalDate fechaFin;
}