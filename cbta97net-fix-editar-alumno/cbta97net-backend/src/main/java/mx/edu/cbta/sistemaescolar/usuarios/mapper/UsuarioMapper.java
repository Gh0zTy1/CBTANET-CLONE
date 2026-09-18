package mx.edu.cbta.sistemaescolar.usuarios.mapper;

import mx.edu.cbta.sistemaescolar.usuarios.dto.RegistrarUsuarioDTO;
import mx.edu.cbta.sistemaescolar.usuarios.dto.UsuarioDTO;

import mx.edu.cbta.sistemaescolar.usuarios.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    @Mapping(target = "roles", ignore = true)
    UsuarioDTO toDTO(Usuario usuario);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permisos", ignore = true)
    UsuarioDTO toDTO(RegistrarUsuarioDTO registrarUsuarioDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Usuario toEntity(RegistrarUsuarioDTO registrarUsuarioDTO);

    @Mapping(target = "roles", ignore = true)
    Usuario toEntity(UsuarioDTO usuarioDto);
}