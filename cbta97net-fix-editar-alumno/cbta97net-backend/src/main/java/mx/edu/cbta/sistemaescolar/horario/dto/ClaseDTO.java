package mx.edu.cbta.sistemaescolar.horario.dto;

import mx.edu.cbta.sistemaescolar.estructura.dto.CicloEscolarDTO;
import mx.edu.cbta.sistemaescolar.estructura.dto.MateriaDTO;
import mx.edu.cbta.sistemaescolar.estructura.dto.AulaDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

/**
 * DTO para crear una clase.
 * Contiene solo los IDs de las entidades relacionadas y los horarios.
 * Este DTO se usa dentro de CrearGrupoConClasesDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaseDTO {

    @JsonProperty("materia_id")
    @NotNull(message = "El ID de la materia es requerido")
    private Long materiaId;

    @JsonProperty("docente_id")
    @NotNull(message = "El ID del docente es requerido")
    private Long docenteId;

    @JsonProperty("grupo_id")
    @NotNull(message = "El ID del grupo es requerido")
    private Long grupoId;

    @JsonProperty("aula_id")
    @NotNull(message = "El ID del aula es requerida")
    private Long aulaId;

    @JsonProperty("ciclo_escolar_id")
    private Long cicloEscolarId;

    @JsonProperty("horario")
    @NotNull(message = "El horario es obligatorio.")
    private HorarioDTO horario;

    // ----- DTOs -----
    // ----- DTOs -----
    // ----- DTOs -----

    @JsonProperty("materia")
    private MateriaDTO materiaDTO;

    @JsonProperty("aula")
    private AulaDTO aulaDTO;

    @JsonProperty("ciclo_escolar")
    private CicloEscolarDTO cicloEscolarDTO;

    @JsonProperty("docente_nombre")
    private String nombreDocente;

    @JsonProperty("docente_apellido_paterno")
    private String apellidoPaternoDocente;

    @JsonProperty("docente_apellido_materno")
    private String apellidoMaternoDocente;

    public String getNombreCompletoDocente() {
        return String.format("%s %s %s",
                (this.nombreDocente != null && !this.nombreDocente.isBlank()) ? this.nombreDocente : "N/A",
                (this.apellidoPaternoDocente != null && !this.apellidoPaternoDocente.isBlank()) ? this.apellidoPaternoDocente : "N/A",
                (this.apellidoMaternoDocente != null && !this.apellidoMaternoDocente.isBlank()) ? this.apellidoMaternoDocente : "N/A"
                );
    }
}
