package mx.edu.cbta.sistemaescolar.estructura.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "aulas")
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="clave", nullable = false, length = 50, unique = true)
    private String clave;

}

    /*
    @Column(nullable = false)
    private Integer capacidad;
     */