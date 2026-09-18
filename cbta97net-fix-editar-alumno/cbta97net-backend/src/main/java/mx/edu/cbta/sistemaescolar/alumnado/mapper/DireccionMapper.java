package mx.edu.cbta.sistemaescolar.alumnado.mapper;

import mx.edu.cbta.sistemaescolar.alumnado.domain.model.Direccion;
import mx.edu.cbta.sistemaescolar.alumnado.dto.DireccionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DireccionMapper {

    Direccion toEntity(DireccionDTO dto);

    DireccionDTO toDTO(Direccion entity);
}