package mx.edu.cbta.sistemaescolar.alumnado.mapper;

import mx.edu.cbta.sistemaescolar.alumnado.dto.TutorDTO;
import mx.edu.cbta.sistemaescolar.alumnado.domain.model.Tutor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TutorMapper {

    @Mapping(target = "alumnos", ignore = true) // Ignorar relación inversa
    @Mapping(source = "parentesco", target = "parentezco")
    Tutor toEntity(TutorDTO dto);

    @Mapping(source = "parentezco", target = "parentesco")
    TutorDTO toDTO(Tutor entity);
}