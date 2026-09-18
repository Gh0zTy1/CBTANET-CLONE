package mx.edu.cbta.sistemaescolar.alumnado.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlumnoDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("matricula")
    private String matricula;

    @JsonProperty("curp")
    private String curp;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellido_paterno")
    private String apellidoPaterno;

    @JsonProperty("apellido_materno")
    private String apellidoMaterno;

    @JsonProperty("fecha_nacimiento")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd['T'HH:mm:ss.SSS'Z']")
    private LocalDate fechaNacimiento;

    @JsonProperty("nss")
    private String numeroSeguroSocial;

    @JsonProperty("poliza_seguro")
    private String numeroPolizaSeguro;

    @JsonProperty("generacion")
    private String generacion;

    @JsonProperty("semestre")
    private Integer semestre;

    @JsonProperty("tutor_legal")
    private TutorDTO tutorLegal;

    @JsonProperty("direccion")
    private DireccionDTO direccion;

    @JsonProperty("condicion_especial_desc")
    private String condicionEspecialDescripcion;

    @JsonProperty("doc_acta_nacimiento")
    private String documentoActaNacimiento;

    @JsonProperty("doc_certificado_secundaria")
    private String documentoCertificadoSecundaria;

    @JsonProperty("doc_curp")
    private String documentoCURP;

    @JsonProperty("doc_foto_escolar")
    private String documentoFotoEscolar;

    @PrePersist
    @PreUpdate
    public void calcularFechaDesdeCurp() {
        if (this.curp == null || this.curp.trim().isEmpty()) {
            return;
        }

        String curpLimpia = this.curp.replaceAll("[^A-Za-z0-9]", "").toUpperCase();

        if (curpLimpia.length() < 10) {
            return;
        }

        try {
            String fechaStr = curpLimpia.substring(4, 10);
            int anioCorto = Integer.parseInt(fechaStr.substring(0, 2));
            int mes = Integer.parseInt(fechaStr.substring(2, 4));
            int dia = Integer.parseInt(fechaStr.substring(4, 6));

            int siglo = (curpLimpia.length() >= 17 && !Character.isDigit(curpLimpia.charAt(16))) ? 2000 : 1900;

            // Manejo manual para evitar errores de LocalDate fuera de rango
            this.fechaNacimiento = LocalDate.of(siglo + anioCorto, mes, dia);

            // LOG DE DEPURACIÓN (Revisa tu consola de Spring)
            System.out.println("✅ Fecha calculada para " + this.nombre + ": " + this.fechaNacimiento);

        } catch (Exception e) {
            System.err.println("❌ Error en CURP: " + this.curp + " -> " + e.getMessage());
            this.fechaNacimiento = null;
        }
    }
}