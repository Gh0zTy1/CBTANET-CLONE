package mx.edu.cbta.sistemaescolar.personal.mapper;

import mx.edu.cbta.sistemaescolar.personal.dto.DocenteDTO;
import mx.edu.cbta.sistemaescolar.personal.dto.RegistrarDocenteDTO;
import mx.edu.cbta.sistemaescolar.personal.domain.model.Docente;

import mx.edu.cbta.sistemaescolar.usuarios.service.UsuarioService;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class DocenteMapper {

    @Autowired
    protected UsuarioService usuarioService;

    @Mappings({
            @Mapping(target = "idDocente", source = "id"),
            @Mapping(target = "usuarioId", source = "usuarioId"),
            // Importante: Especificar 'docente.usuarioId' para evitar ambigüedad
            @Mapping(target = "nombre", expression = "java(usuarioService.obtenerNombre(docente.getUsuarioId()))"),
            @Mapping(target = "apellidoPaterno", expression = "java(usuarioService.obtenerApellidoPaterno(docente.getUsuarioId()))"),
            @Mapping(target = "apellidoMaterno", expression = "java(usuarioService.obtenerApellidoMaterno(docente.getUsuarioId()))"),
            @Mapping(target = "email", expression = "java(usuarioService.obtenerCorreoElectronico(docente.getUsuarioId()))"),
            @Mapping(target = "curp", ignore = true),
            @Mapping(target = "telefono", ignore = true),
            @Mapping(target = "activo", ignore = true),
            @Mapping(target = "roles", ignore = true),
            @Mapping(target = "claseIds", ignore = true),
            @Mapping(target = "materiaNombres", ignore = true)
    })
    public abstract DocenteDTO toDto(Docente docente);

    // Mapeo para el registro inicial
    @Mapping(target = "idDocente", ignore = true)
    @Mapping(target = "usuarioId", source = "usuarioId")
    public abstract DocenteDTO toDto(RegistrarDocenteDTO registrarDocenteDTO);

    @Mappings({
            @Mapping(target = "materiasCalificadasIds", ignore = true),
            @Mapping(target = "usuarioId", source = "docenteDto.usuarioId"), // Especificamos el parámetro
            @Mapping(target = "id", source = "docenteDto.idDocente")
    })
    public abstract Docente toEntity(DocenteDTO docenteDto);
}