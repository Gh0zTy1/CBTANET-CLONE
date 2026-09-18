package mx.edu.cbta.sistemaescolar.grupos.mapper;

import mx.edu.cbta.sistemaescolar.grupos.dto.CrearNuevoGrupoDTO;
import mx.edu.cbta.sistemaescolar.grupos.dto.GrupoDTO;
import mx.edu.cbta.sistemaescolar.grupos.domain.model.Grupo;

import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class GrupoMapper {

    // 1. De Entidad a GrupoDTO (Para respuestas de API)
    @Mapping(source = "cicloEscolarId", target = "cicloEscolarDTO.id")
    @Mapping(source = "carreraTecnicaId", target = "carreraTecnicaDTO.id")
    @Mapping(source = "areaPropedeuticaId", target = "areaPropedeuticaDTO.id")
    @Mapping(target = "clases", ignore = true)
    public abstract GrupoDTO toDTO(Grupo grupo);

    // 2. De GrupoDTO a Entidad (Para actualizaciones generales)
    @Mapping(source = "cicloEscolarDTO.id", target = "cicloEscolarId")
    @Mapping(source = "carreraTecnicaDTO.id", target = "carreraTecnicaId")
    @Mapping(source = "areaPropedeuticaDTO.id", target = "areaPropedeuticaId")
    @Mapping(target = "id", ignore = true)
    public abstract Grupo toEntity(GrupoDTO grupoDto);

    // 3. De CrearNuevoGrupoDTO a Entidad (Para el flujo de creación)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cicloEscolarId", ignore = true)
    public abstract Grupo toEntity(CrearNuevoGrupoDTO crearNuevoGrupoDTO);

    // 4. De CrearNuevoGrupoDTO a GrupoDTO (Si necesitas transformar entre DTOs)
    @Mapping(target = "cicloEscolarDTO", ignore = true)
    @Mapping(source = "carreraTecnicaId", target = "carreraTecnicaDTO.id")
    @Mapping(source = "areaPropedeuticaId", target = "areaPropedeuticaDTO.id")
    @Mapping(source = "clases", target = "clases")
    public abstract GrupoDTO toDTO(CrearNuevoGrupoDTO crearNuevoGrupoDTO);
}