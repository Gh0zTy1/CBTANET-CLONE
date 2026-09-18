package mx.edu.cbta.sistemaescolar.horario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.edu.cbta.sistemaescolar.estructura.dto.AulaDTO;
import mx.edu.cbta.sistemaescolar.estructura.dto.CicloEscolarDTO;
import mx.edu.cbta.sistemaescolar.estructura.dto.ActividadParaescolarDTO;

/**
 * DTO para crear una clase.
 * Contiene solo los IDs de las entidades relacionadas y los horarios.
 * Este DTO se usa dentro de CrearGrupoConClasesDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaseParaescolarDTO {

    @JsonProperty("actividad_paraescolar_id")
    @NotNull(message = "El ID de la actividad paraescolar es requerido")
    private Long actividadParaescolarId;

    @JsonProperty("actividad_paraescolar")
    private ActividadParaescolarDTO actividadDTO;

    @JsonProperty("docente_id")
    @NotNull(message = "El ID del docente es requerido")
    private Long docenteId;

    @JsonProperty("grupo_paraescolar_id")
    @NotNull(message = "El ID del grupo paraescolar es requerido")
    private Long grupoParaescolarId;

    @JsonProperty("aula_id")
    @NotNull(message = "El ID del aula o espacio es requerida")
    private Long aulaId;

    @JsonProperty("aula")
    private AulaDTO aulaDTO;

    @JsonProperty("ciclo_escolar_id")
    private Long cicloEscolarId;

    @JsonProperty("ciclo_escolar")
    private CicloEscolarDTO cicloEscolarDTO;

    @JsonProperty("docente_nombre")
    private String nombreDocente;

    @JsonProperty("docente_apellido_paterno")
    private String apellidoPaternoDocente;

    @JsonProperty("docente_apellido_materno")
    private String apellidoMaternoDocente;

    @JsonProperty("horario")
    @NotNull(message = "El horario es obligatorio.")
    private HorarioDTO horario;

    public String getNombreCompletoDocente() {
        return String.format("%s %s %s",
                (this.nombreDocente != null && !this.nombreDocente.isBlank()) ? this.nombreDocente : "N/A",
                (this.apellidoPaternoDocente != null && !this.apellidoPaternoDocente.isBlank()) ? this.apellidoPaternoDocente : "N/A",
                (this.apellidoMaternoDocente != null && !this.apellidoMaternoDocente.isBlank()) ? this.apellidoMaternoDocente : "N/A"
                );
    }
}
