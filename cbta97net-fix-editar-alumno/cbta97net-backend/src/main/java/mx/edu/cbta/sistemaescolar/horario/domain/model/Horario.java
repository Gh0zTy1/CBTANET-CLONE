package mx.edu.cbta.sistemaescolar.horario.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Data
@Embeddable
public class Horario {

    @Enumerated(EnumType.STRING)
    @Column(name = "dia", nullable = false)
    private DiaSemana dia;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    /**
     * Verifica si este horario se empalma (traslapa) con otro horario dado.
     *
     * @param otro El otro horario a comparar.
     * @return true si ambos horarios ocurren el mismo día y se empalman en el tiempo, false en caso contrario.
     */
    public boolean seEmpalmaCon(Horario otro) {
        if (otro == null || this.dia == null || otro.getDia() == null ||
                this.horaInicio == null || this.horaFin == null ||
                otro.getHoraInicio() == null || otro.getHoraFin() == null) {
            return false;
        }

        if (this.dia != otro.getDia()) {
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