package mx.edu.cbta.sistemaescolar.estructura.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "actividades_paraescolares")
public class ActividadParaescolar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private boolean isActivo = true;
}