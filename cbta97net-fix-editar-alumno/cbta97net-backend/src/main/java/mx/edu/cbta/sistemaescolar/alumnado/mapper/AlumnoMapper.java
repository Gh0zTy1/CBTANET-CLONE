package mx.edu.cbta.sistemaescolar.alumnado.mapper;

import mx.edu.cbta.sistemaescolar.alumnado.dto.AlumnoDTO;
import mx.edu.cbta.sistemaescolar.alumnado.domain.model.Alumno;

import mx.edu.cbta.sistemaescolar.alumnado.dto.EditarInformacionAlumnoDTO;
import mx.edu.cbta.sistemaescolar.alumnado.dto.SolicitarAlumnoDetalleDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = { TutorMapper.class })
public abstract class AlumnoMapper {

    @Mappings({
            @Mapping(source = "tutorLegal", target = "tutorLegal"),
    })
    public abstract SolicitarAlumnoDetalleDTO toDetalleDTO(Alumno entity);

    @Mappings({
            @Mapping(source = "tutorLegal", target = "tutorLegal"),
            @Mapping(source = "direccion", target = "direccion")
    })
    public abstract AlumnoDTO toDTO(Alumno entity);

    @Mappings({
            @Mapping(source = "tutorLegal", target = "tutorLegal"),
            @Mapping(source = "direccion", target = "direccion"),
            @Mapping(source = "numeroSeguroSocial", target = "numeroSeguroSocial"),
            @Mapping(source = "numeroPolizaSeguro", target = "numeroPolizaSeguro"),
            @Mapping(source = "condicionEspecialDescripcion", target = "condicionEspecialDescripcion")
    })
    public abstract AlumnoDTO toDTO(EditarInformacionAlumnoDTO dto);

    @Mappings({
            @Mapping(target = "id", ignore = false),
            @Mapping(target = "tutorAcademicoId", ignore = true),
            @Mapping(source = "direccion", target = "direccion")
    })
    public abstract Alumno toEntity(AlumnoDTO alumnoDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tutorAcademicoId", ignore = true)
    public abstract void updateEntityFromDto(AlumnoDTO dto, @org.mapstruct.MappingTarget Alumno entity);
}