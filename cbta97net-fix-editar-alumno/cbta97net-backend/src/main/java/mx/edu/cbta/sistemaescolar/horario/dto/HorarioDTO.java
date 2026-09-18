package mx.edu.cbta.sistemaescolar.horario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.time.LocalTime;

@Data
public class HorarioDTO {


    @JsonProperty("dia_semana")
    @NotNull(message = "El día de la semana para el horario es obligatorio.")
    private String dia;

    @JsonProperty("hora_inicio")
    @JsonFormat(pattern = "HH:mm:ss")
    @NotNull(message = "La hora de inicio no es válida.")
    private LocalTime horaInicio;

    @JsonProperty("hora_fin")
    @JsonFormat(pattern = "HH:mm:ss")
    @NotNull(message = "La hora de finalización no es válida.")
    private LocalTime horaFin;

    /**
     * Verifica si este horario se empalma (traslapa) con otro horario dado.
     *
     * @param otro El otro horario a comparar.
     * @return true si ambos horarios ocurren el mismo día y se empalman en el tiempo, false en caso contrario.
     */
    public boolean seEmpalmaCon(HorarioDTO otro) {

        if (this.dia == null || otro == null || otro.getDia() == null) {
            return false;
        }

        if (!this.dia.equalsIgnoreCase(otro.getDia())) {
            return false;
        }

        return this.horaInicio.isBefore(otro.getHoraFin()) &&
                this.horaFin.isAfter(otro.getHoraInicio());
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return String.format("%s [%s - %s]",
                dia,
                horaInicio.format(formatter),
                horaFin.format(formatter)
        );
    }
}