package mx.edu.cbta.sistemaescolar.estructura.mapper;

import mx.edu.cbta.sistemaescolar.estructura.dto.UnidadDTO;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.Unidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UnidadMapper {

    UnidadMapper INSTANCE = Mappers.getMapper(UnidadMapper.class);

    /**
     * Convierte una entidad Unidad a un UnidadDto.
     * @param unidad La entidad a convertir.
     * @return El DTO resultante.
     */
    UnidadDTO toDto(Unidad unidad);

    /**
     * Convierte un UnidadDto a una entidad Unidad.
     * La relación inversa 'materia' se ignora intencionadamente, ya que
     * debe ser gestionada por la capa de servicio al crear/actualizar.
     * @param unidadDto El DTO a convertir.
     * @return La entidad resultante.
     */
    @Mapping(target = "materia", ignore = true)
    Unidad toEntity(UnidadDTO unidadDto);
}