package mx.edu.cbta.sistemaescolar.horario.mapper;

import mx.edu.cbta.sistemaescolar.horario.dto.ClaseDTO;
import mx.edu.cbta.sistemaescolar.horario.domain.model.Clase;

import org.mapstruct.Mappings;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {HorarioMapper.class})
public abstract class ClaseMapper {

    @Mappings({
            @Mapping(source = "materiaId", target = "materiaId"),
            @Mapping(source = "aulaId", target = "aulaId"),
            @Mapping(source = "cicloEscolarId", target = "cicloEscolarId"),
            // Ignoramos campos que no existen en la entidad Clase pero sí en el DTO
            @Mapping(target = "materiaDTO", ignore = true),
            @Mapping(target = "aulaDTO", ignore = true),
            @Mapping(target = "cicloEscolarDTO", ignore = true),
            @Mapping(target = "nombreDocente", ignore = true),
            @Mapping(target = "apellidoPaternoDocente", ignore = true),
            @Mapping(target = "apellidoMaternoDocente", ignore = true)
    })
    public abstract ClaseDTO toDTO(Clase clase);

    @Mappings({
            @Mapping(source = "materiaId", target = "materiaId"),
            @Mapping(source = "aulaId", target = "aulaId"),
            @Mapping(source = "cicloEscolarId", target = "cicloEscolarId")
    })
    public abstract Clase toEntity(ClaseDTO claseDTO);
}