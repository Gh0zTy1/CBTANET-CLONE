package mx.edu.cbta.sistemaescolar.gruposparaescolares.mapper;

import mx.edu.cbta.sistemaescolar.gruposparaescolares.dto.AlumnoInscritoParaescolarDTO;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.model.AlumnoInscritoParaescolar;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlumnoInscritoParaescolarMapper {

    AlumnoInscritoParaescolarDTO toDTO(AlumnoInscritoParaescolar entidad);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaBaja", ignore = true)
    AlumnoInscritoParaescolar toEntity(AlumnoInscritoParaescolarDTO dto);
}