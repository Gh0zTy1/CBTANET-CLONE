package mx.edu.cbta.sistemaescolar.estructura.mapper;

import mx.edu.cbta.sistemaescolar.estructura.dto.AulaDTO;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.Aula;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AulaMapper {

    AulaMapper INSTANCE = Mappers.getMapper(AulaMapper.class);

    AulaDTO toDTO(Aula aula);

    Aula toEntity(AulaDTO aulaDto);
}
