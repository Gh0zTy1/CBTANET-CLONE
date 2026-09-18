package mx.edu.cbta.sistemaescolar.estructura.mapper;

import mx.edu.cbta.sistemaescolar.estructura.dto.ActividadParaescolarDTO;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.ActividadParaescolar;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

// [Diagrama de Secuencia] Lifeline: :ActividadParaescolarMapper
@Mapper(componentModel = "spring")
public interface ActividadParaescolarMapper {

    ActividadParaescolarMapper INSTANCE = Mappers.getMapper(ActividadParaescolarMapper.class);

    ActividadParaescolarDTO toDTO(ActividadParaescolar actividadParaescolar);

    ActividadParaescolar toEntity(ActividadParaescolarDTO actividadParaescolarDTO);
}