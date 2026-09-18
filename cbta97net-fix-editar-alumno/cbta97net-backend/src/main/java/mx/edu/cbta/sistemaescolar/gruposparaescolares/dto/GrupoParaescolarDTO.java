package mx.edu.cbta.sistemaescolar.gruposparaescolares.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import mx.edu.cbta.sistemaescolar.horario.dto.ClaseParaescolarDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrupoParaescolarDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nota")
    @Size(max = 300, message = "La nota no puede exceder 300 caracteres.")
    private String nota;

    @JsonProperty("maximo_espacios")
    private Integer maximoEspacios;

    @JsonProperty("ciclo_escolar_id")
    @NotNull(message = "El ciclo escolar es requerido.")
    private Long cicloEscolarId;

    @JsonProperty("actividad_paraescolar_id")
    @NotNull(message = "La actividad paraescolar es obligatoria.")
    private Long actividadParaescolarId;

    @JsonProperty("docente_id")
    @NotNull(message = "El instructor es obligatorio.")
    private Long docenteId;

    @JsonProperty("clases")
    private List<ClaseParaescolarDTO> clases;

    @JsonProperty("alumnos_inscritos")
    private Set<String> alumnosInscritos;

    @JsonProperty("cupos_disponibles")
    private Integer cuposDisponibles;
}