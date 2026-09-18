package mx.edu.cbta.sistemaescolar.horario.mapper;

import mx.edu.cbta.sistemaescolar.horario.dto.ClaseParaescolarDTO;
import mx.edu.cbta.sistemaescolar.horario.domain.model.ClaseParaescolar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {HorarioMapper.class})
public abstract class ClaseParaescolarMapper {

    @Mappings({
            @Mapping(source = "actividadParaescolarId", target = "actividadParaescolarId"),
            @Mapping(source = "cicloEscolarId", target = "cicloEscolarId"),
            @Mapping(target = "actividadDTO", ignore = true),
            @Mapping(target = "aulaDTO", ignore = true),
            @Mapping(target = "cicloEscolarDTO", ignore = true),
            @Mapping(target = "nombreDocente", ignore = true),
            @Mapping(target = "apellidoPaternoDocente", ignore = true),
            @Mapping(target = "apellidoMaternoDocente", ignore = true),
    })
    public abstract ClaseParaescolarDTO toDTO(ClaseParaescolar clase);

    @Mappings({
            @Mapping(source = "actividadParaescolarId", target = "actividadParaescolarId"),
            @Mapping(source = "cicloEscolarId", target = "cicloEscolarId")
    })
    public abstract ClaseParaescolar toEntity(ClaseParaescolarDTO dto);
}