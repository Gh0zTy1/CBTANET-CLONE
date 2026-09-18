package mx.edu.cbta.sistemaescolar.alumnado.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Direccion {

    @Column(name = "d_calle", nullable = true, length = 120)
    private String calle;

    @Column(name = "d_colonia", nullable = true, length = 120)
    private String colonia;

    @Column(name = "d_numero_exterior", nullable = true, length = 20)
    private String numeroExterior;

    @Column(name = "d_codigo_postal", nullable = true, length = 10)
    private String codigoPostal;

    @Column(name = "d_localidad", nullable = true, length = 10)
    private String localidad;
}
