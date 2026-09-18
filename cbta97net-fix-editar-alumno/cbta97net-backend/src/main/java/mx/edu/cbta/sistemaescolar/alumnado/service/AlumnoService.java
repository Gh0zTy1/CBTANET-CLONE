package mx.edu.cbta.sistemaescolar.alumnado.service;

import mx.edu.cbta.sistemaescolar.alumnado.dto.InformacionBasicaAlumnoDTO;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.*;
import mx.edu.cbta.sistemaescolar.alumnado.dto.AlumnoDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface AlumnoService {
    AlumnoDTO obtenerAlumnoPorMatricula(String matricula) throws AlumnoNoEncontradoException;

    AlumnoDTO registrarAlumno(AlumnoDTO alumno) throws AlumnoException, RegistrarAlumnoException;

    String obtenerMatriculaPorId(Long id);

    AlumnoDTO actualizarAlumno(AlumnoDTO alumno);

    void eliminarAlumno(String matricula) throws AlumnoNoEncontradoException, EliminarAlumnoException;

    Page<AlumnoDTO> obtenerAlumnosTodos(Pageable pageable);

    AlumnoDTO obtenerAlumnoPorCurp(String curp);

    Page<InformacionBasicaAlumnoDTO> obtenerAlumnosPorCredenciales(String credenciales, Pageable pageable);

    void importarAlumnos(MultipartFile file) throws ImportarAlumnosException;
}