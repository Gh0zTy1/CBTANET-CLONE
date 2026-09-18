package mx.edu.cbta.sistemaescolar.alumnado.dto;

public record InformacionBasicaAlumnoDTO(
        Long id,
        String matricula,
        String nombre,
        String apellido_paterno,
        String apellido_materno,
        String curp
) {}