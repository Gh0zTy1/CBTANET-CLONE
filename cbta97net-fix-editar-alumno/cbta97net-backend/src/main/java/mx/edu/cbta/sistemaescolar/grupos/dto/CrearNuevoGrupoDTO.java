package mx.edu.cbta.sistemaescolar.grupos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import mx.edu.cbta.sistemaescolar.estructura.dto.AreaPropedeuticaDTO;
import mx.edu.cbta.sistemaescolar.estructura.dto.CarreraTecnicaDTO;
import mx.edu.cbta.sistemaescolar.estructura.dto.CicloEscolarDTO;
import mx.edu.cbta.sistemaescolar.horario.dto.ClaseDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * DTO para crear un grupo con sus clases incluidas.
 * Este DTO se utiliza en el endpoint de creación para recibir
 * toda la información necesaria del grupo y sus clases asociadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrearNuevoGrupoDTO {

    @Size(max = 300, message = "La nota no puede exceder 300 caracteres")
    @JsonProperty("nota")
    private String nota;

    @NotNull(message = "La letra del grupo es obligatoria.")
    @JsonProperty("letra")
    private Character letra;

    @JsonProperty("semestre")
    @NotNull(message = "El semestre es requerido.")
    private int semestre;

    @NotNull(message = "El turno es requerido.")
    @JsonProperty("turno")
    private String turno;

    @JsonProperty("carrera_id")
    private Long carreraTecnicaId;

    @JsonProperty("area_id")
    private Long areaPropedeuticaId;

    @JsonProperty("ciclo")
    private CicloEscolarDTO cicloEscolarDTO;

    @JsonProperty("carrera")
    private CarreraTecnicaDTO carreraTecnicaDTO;

    @JsonProperty("area")
    private AreaPropedeuticaDTO areaPropedeuticaDTO;

    /**
     * Lista de clases que se crearán junto con el grupo.
     * Cada ClaseDTO debe contener los IDs de las entidades relacionadas
     * (materia, docente, aula) y los horarios correspondientes.
     */
    //@NotNull(message = "La lista de clases es requerida")
    @Size(min = 1, message = "Debe incluir al menos una clase")
    @JsonProperty("clases")
    private List<ClaseDTO> clases;
}
