package mx.edu.cbta.sistemaescolar.grupos.dto;

import mx.edu.cbta.sistemaescolar.estructura.dto.AreaPropedeuticaDTO;
import mx.edu.cbta.sistemaescolar.estructura.dto.CarreraTecnicaDTO;
import mx.edu.cbta.sistemaescolar.estructura.dto.CicloEscolarDTO;
import mx.edu.cbta.sistemaescolar.horario.dto.ClaseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class GrupoDTO {

    @JsonProperty("id")
    private Long id;

    @Size(max = 300, message = "La nota no puede exceder 300 caracteres.")
    @JsonProperty("nota")
    private String nota;

    @NotNull(message = "La letra del grupo es obligatoria.")
    @JsonProperty("letra")
    private Character letra;

    @NotNull(message = "El semestre es requerido.")
    @JsonProperty("semestre")
    private int semestre;

    @JsonProperty("turno")
    @NotNull(message = "El turno es requerido.")
    private String turno;

    @JsonProperty("ciclo_id")
    private Long cicloEscolarId;

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

    public static String obtenerNombreSemestre(int semestre) {
        return switch (semestre) {
            case 1 -> "Primer semestre";
            case 2 -> "Segundo semestre";
            case 3 -> "Tercer semestre";
            case 4 -> "Cuarto semestre";
            case 5 -> "Quinto semestre";
            case 6 -> "Sexto semestre";
            default -> "Semestre inválido";
        };
    }

    public String obtenerNombreSemestre() {
        return switch (this.semestre) {
            case 1 -> "Primer semestre";
            case 2 -> "Segundo semestre";
            case 3 -> "Tercer semestre";
            case 4 -> "Cuarto semestre";
            case 5 -> "Quinto semestre";
            case 6 -> "Sexto semestre";
            default -> "Semestre inválido";
        };
    }
}
