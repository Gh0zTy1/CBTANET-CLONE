package mx.edu.cbta.sistemaescolar.estructura.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AulaDTO {
    private Long id;
    private String clave;
}