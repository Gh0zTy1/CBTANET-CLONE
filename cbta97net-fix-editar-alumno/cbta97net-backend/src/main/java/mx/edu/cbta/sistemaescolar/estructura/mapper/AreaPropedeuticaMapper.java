package mx.edu.cbta.sistemaescolar.estructura.mapper;

import mx.edu.cbta.sistemaescolar.estructura.dto.AreaPropedeuticaDTO;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.AreaPropedeutica;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AreaPropedeuticaMapper {

    AreaPropedeuticaMapper INSTANCE = Mappers.getMapper(AreaPropedeuticaMapper.class);

    AreaPropedeuticaDTO toDto(AreaPropedeutica areaPropedeutica);

    @Mapping(target = "materias", ignore = true)
    AreaPropedeutica toEntity(AreaPropedeuticaDTO areaPropedeuticaDto);
}