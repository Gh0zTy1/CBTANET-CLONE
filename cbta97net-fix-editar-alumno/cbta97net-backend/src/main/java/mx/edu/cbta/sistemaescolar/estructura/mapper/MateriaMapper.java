package mx.edu.cbta.sistemaescolar.estructura.mapper;

import mx.edu.cbta.sistemaescolar.estructura.dto.MateriaDTO;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.Materia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {UnidadMapper.class})
public interface MateriaMapper {

    MateriaMapper INSTANCE = Mappers.getMapper(MateriaMapper.class);

    @Mapping(source = "carreraTecnica.id", target = "carreraTecnicaId")
    @Mapping(source = "areaPropedeutica.id", target = "areaPropedeuticaId")
    MateriaDTO toDto(Materia materia);

    @Mapping(target = "unidades", ignore = true)
    Materia toEntity(MateriaDTO materiaDTO);
}
