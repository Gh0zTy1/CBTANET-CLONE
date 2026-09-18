package mx.edu.cbta.sistemaescolar.usuarios.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Locale;
import java.util.Set;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "usuario_seq")
    @TableGenerator(
            name = "usuario_seq",
            table = "sys_sequences",
            pkColumnName = "sequence_name",
            valueColumnName = "next_val",
            pkColumnValue = "usuario_id",
            initialValue = 100000,
            allocationSize = 1
    )
    private Long id;

    @Column(name = "curp", length = 18)
    private String curp;

    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "apellido_paterno", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column(name = "email", unique = true, length = 70, nullable = false)
    private String email;

    @Column(name = "telefono", unique = true, length = 20, nullable = false)
    private String telefono;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    /**
     * Nombre de usuario usado para el inicio de sesión en Keycloak
     */
    @Transient
    private String keycloakUsername;

    /**
     * Contendrá los roles almacenados para el usuario en Keycloak
     */
    @Transient
    private Set<String> roles;

    /**
     * Esta contrasena vive en keycloak.
     */
    @Transient
    private String contrasena;

    /**
     * Obtiene el nombre completo del usuario. Concatena los nombres y apellidos (paterno y materno)
     * del usuario. En caso de que no se cuente con alguno, devolvera por cada cadena "N/A" para indicar
     * que no se encontro el recurso (nombre, apellido paterno o apellido materno).
     * @return Nombre completo del usuario.
     */
    public String getNombreCompleto() {
        return String.format(
                "%s %s %s",
                (this.nombre != null) ? this.nombre.toUpperCase() : "N/A",
                (this.apellidoPaterno != null) ? this.apellidoPaterno : "N/A",
                (this.apellidoMaterno != null) ? this.apellidoMaterno : "N/A"
        );
    }
}