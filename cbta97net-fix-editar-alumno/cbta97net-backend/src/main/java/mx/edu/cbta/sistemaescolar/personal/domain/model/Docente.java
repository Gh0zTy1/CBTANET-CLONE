package mx.edu.cbta.sistemaescolar.personal.domain.model;

import java.util.Set;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "docentes")
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "cedula_profesional", nullable = false, length = 10)
    private String cedulaProfesional;

    /**
     * Referencia lógica al ID del Usuario.
     * Al ser un Long, el módulo 'personal' no necesita importar la clase 'Usuario'.
     * updatable = false garantiza que no cambie después de la creación.
     */
    @Column(name = "usuario_id", nullable = false, updatable = false, unique = true)
    private Long usuarioId;

    @ElementCollection
    @CollectionTable(name = "docentes_materias", joinColumns = @JoinColumn(name = "docente_id"))
    @Column(name = "materia_id")
    private Set<Long> materiasCalificadasIds;
}