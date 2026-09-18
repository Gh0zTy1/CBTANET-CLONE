package mx.edu.cbta.sistemaescolar.alumnado.domain.model;


import jakarta.persistence.*;


import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name="alumnos")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id; // Nuevo ID independiente

    @Column(name = "matricula", length = 20, unique = true, nullable = false)
    private String matricula;

    @Column(name = "curp", nullable = false, length = 20, unique = true)
    private String curp;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "nss", length = 20, unique = true)
    private String numeroSeguroSocial;

    @Column(name = "poliza_seguro_escuela", length = 50)
    private String numeroPolizaSeguro;

    @Column(name = "generacion", nullable = true, length = 30)
    private String generacion;

    @Column(name = "semestre", nullable = true, length = 30)
    private Integer semestre;

    @Embedded
    private Direccion direccion;

    @Lob
    @Column(name = "condicion_especial_descripcion")
    private String condicionEspecialDescripcion;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_legal_id")
    @ToString.Exclude
    private Tutor tutorLegal;

    @Column(name = "tutor_academico_id")
    private Long tutorAcademicoId;

    @PrePersist
    @PreUpdate
    public void calcularFechaDesdeCurp() {
        if (this.curp == null || this.curp.trim().length() < 10) return;

        String limpia = this.curp.trim().toUpperCase();

        try {
            int anioCorto = Integer.parseInt(limpia.substring(4, 6));
            int mes = Integer.parseInt(limpia.substring(6, 8));
            int dia = Integer.parseInt(limpia.substring(8, 10));

            // Lógica de siglo mejorada:
            // Si el año es mayor al año actual (ej. 26), debe ser 1900.
            // Si el año es 00-26, es 2000.
            int anioActualCorto = LocalDate.now().getYear() % 100;
            int siglo = (anioCorto <= anioActualCorto) ? 2000 : 1900;

            LocalDate fechaNac = LocalDate.of(siglo + anioCorto, mes, dia);

            this.fechaNacimiento = fechaNac;

            System.out.println("#### FECHA NACIMIENTO CALCULADA: %s".formatted(fechaNac));

        } catch (Exception e) {
            // Si esto sale en consola, sabremos qué CURP exacta falló
            System.err.println("❌ Error procesando fecha para: " + limpia + " - " + e.getMessage());
        }
    }
}