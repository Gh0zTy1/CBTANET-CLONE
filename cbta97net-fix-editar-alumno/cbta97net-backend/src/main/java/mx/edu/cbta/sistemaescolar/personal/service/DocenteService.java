package mx.edu.cbta.sistemaescolar.personal.service;


import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteDuplicadoException;
import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteException;
import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteNoEncontradoException;
import mx.edu.cbta.sistemaescolar.personal.domain.exception.RegistrarDocenteException;
import mx.edu.cbta.sistemaescolar.personal.dto.DocenteDTO;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface DocenteService {

    DocenteDTO registrarDocente(DocenteDTO nuevoDocenteDTO) throws RegistrarDocenteException, DocenteDuplicadoException;

    Page<DocenteDTO> obtenerTodos(Pageable pageable) throws DocenteException;

    DocenteDTO obtenerDocentePorId(Long id) throws DocenteNoEncontradoException;

    String obtenerNombre(Long idDocente);

    String obtenerApellidoPaterno(Long idDocente);

    String obtenerApellidoMaterno(Long idDocente);

    boolean existeDocentePorId(Long idDocente);

    boolean existeDocentePorCedula(String cedulaProfesional);

    //boolean docenteDisponibleEnHorario(Long idDocente, Horario horario) throws DocenteNoDisponibleException, DocenteNoEncontradoException;

    Page<DocenteDTO> obtenerDocentePorMateria(Long materiaId, Pageable pageable);
}