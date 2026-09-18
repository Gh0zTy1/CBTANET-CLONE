package mx.edu.cbta.sistemaescolar.alumnado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DireccionDTO {

    @JsonProperty("calle")
    private String calle;

    @JsonProperty("colonia")
    private String colonia;

    @JsonProperty("numero_exterior")
    private String numeroExterior;

    @JsonProperty("codigo_postal")
    private String codigoPostal;

    @JsonProperty("localidad")
    private String localidad;
}