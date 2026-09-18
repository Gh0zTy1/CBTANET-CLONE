package mx.edu.cbta.sistemaescolar.estructura.mapper;

import mx.edu.cbta.sistemaescolar.estructura.dto.CarreraTecnicaDTO;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.CarreraTecnica;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CarreraTecnicaMapper {

    CarreraTecnicaMapper INSTANCE = Mappers.getMapper(CarreraTecnicaMapper.class);

    CarreraTecnicaDTO toDto(CarreraTecnica carreraTecnica);

    @Mapping(target = "materias", ignore = true)
    CarreraTecnica toEntity(CarreraTecnicaDTO carreraTecnicaDto);
}
