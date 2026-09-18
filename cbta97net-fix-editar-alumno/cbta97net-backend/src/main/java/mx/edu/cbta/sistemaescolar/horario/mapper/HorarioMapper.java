package mx.edu.cbta.sistemaescolar.horario.mapper;

import mx.edu.cbta.sistemaescolar.horario.dto.HorarioDTO;
import mx.edu.cbta.sistemaescolar.horario.domain.model.DiaSemana;
import mx.edu.cbta.sistemaescolar.horario.domain.model.Horario;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class HorarioMapper {

    public abstract Horario toEntity(HorarioDTO horarioDTO);

    public abstract HorarioDTO toDTO(Horario horario);

    protected DiaSemana mapDia(String value) {
        if (value == null) return null;

        for (DiaSemana dia : DiaSemana.values()) {
            if (dia.name().equalsIgnoreCase(value)) {
                return dia;
            }
        }

        throw new IllegalArgumentException("Día de la semana no válido: " + value);
    }
}