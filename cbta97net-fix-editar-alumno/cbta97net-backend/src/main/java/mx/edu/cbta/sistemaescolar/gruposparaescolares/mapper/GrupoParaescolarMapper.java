package mx.edu.cbta.sistemaescolar.gruposparaescolares.mapper;

import mx.edu.cbta.sistemaescolar.gruposparaescolares.dto.CrearGrupoParaescolarDTO;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.dto.GrupoParaescolarDTO;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.model.GrupoParaescolar;

import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GrupoParaescolarMapper {

    // Entidad a DTO
    @Mapping(source = "notaIdentificatoria", target = "nota")
    @Mapping(source = "actividadParaescolarId", target = "actividadParaescolarId")
    @Mapping(target = "alumnosInscritos", ignore = true)
    GrupoParaescolarDTO toDTO(GrupoParaescolar entidad);

    // DTO de Creación a DTO de Respuesta
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "alumnosInscritos", ignore = true)
    GrupoParaescolarDTO toDTO(CrearGrupoParaescolarDTO dto);

    // DTO de Creación a Entidad
    @Mapping(source = "nota", target = "notaIdentificatoria")
    @Mapping(source = "actividadParaescolarId", target = "actividadParaescolarId")
    @Mapping(target = "id", ignore = true)
    // Se elimina el ignore de 'clases' porque el error dice que la propiedad no existe en la entidad
    GrupoParaescolar toEntity(CrearGrupoParaescolarDTO dto);

    // DTO de Respuesta a Entidad
    @Mapping(source = "nota", target = "notaIdentificatoria")
    @Mapping(source = "actividadParaescolarId", target = "actividadParaescolarId")
    GrupoParaescolar toEntity(GrupoParaescolarDTO dto);

    // Actualizar Entidad desde DTO
    @Mapping(source = "nota", target = "notaIdentificatoria")
    @Mapping(source = "actividadParaescolarId", target = "actividadParaescolarId")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(GrupoParaescolarDTO dto, @MappingTarget GrupoParaescolar entidad);
}