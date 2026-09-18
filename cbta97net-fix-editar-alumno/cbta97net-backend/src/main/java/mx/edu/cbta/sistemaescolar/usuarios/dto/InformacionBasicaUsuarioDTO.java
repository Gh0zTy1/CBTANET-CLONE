package mx.edu.cbta.sistemaescolar.usuarios.dto;

public record InformacionBasicaUsuarioDTO(
        Long id,
        String curp,
        String nombre,
        String apellido_paterno,
        String apellido_materno
) {}