package mx.edu.cbta.sistemaescolar.alumnado.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@Table(name="tutores_alumnos")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name="nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name="apellido_paterno", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name="apellido_materno", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name="fecha_nacimiento") // Opcional
    private LocalDate fechaNacimiento;

    @Column(name="telefono", nullable = false, length = 20, unique = true)
    private String telefono;

    @Column(name = "parentezco", nullable = true, length = 30)
    private String parentezco;

    // Relación inversa: Un tutor puede tener varios alumnos
    @OneToMany(mappedBy = "tutorLegal")
    @ToString.Exclude
    private Set<Alumno> alumnos;
}