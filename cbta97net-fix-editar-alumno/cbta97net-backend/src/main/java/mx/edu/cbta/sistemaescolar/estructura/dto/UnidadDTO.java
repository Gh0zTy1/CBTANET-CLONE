package mx.edu.cbta.sistemaescolar.estructura.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnidadDTO {
    private Long id;
    private String nombre;
    private Double porcentaje;
}