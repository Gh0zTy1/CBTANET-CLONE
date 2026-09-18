package mx.edu.cbta.sistemaescolar.gruposparaescolares.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import mx.edu.cbta.sistemaescolar.horario.dto.ClaseParaescolarDTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearGrupoParaescolarDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nota")
    private String nota;

    @JsonProperty("maximo_espacios")
    @NotNull(message = "El máximo de espacios es obligatorio")
    @Min(value = 1, message = "El cupo mínimo para abrir un grupo es de 1 alumno")
    //@Max(value = 60, message = "El cupo no puede exceder los 50 alumnos")
    private Integer maximoEspacios;

    @JsonProperty("actividad_paraescolar_id")
    @NotNull(message = "Debe seleccionar la actividad paraescolar")
    private Long actividadParaescolarId;

    @JsonProperty("docente_id")
    @NotNull(message = "Debe asignar un docente al grupo")
    private Long docenteId;

    @JsonProperty("clases")
    @NotEmpty(message = "El grupo debe tener al menos una sesión de clase asignada")
    private List<ClaseParaescolarDTO> clases;
}