package mx.edu.cbta.sistemaescolar.horario.service;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ParaescolarNoEncontradaException;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CicloEscolarNoEncontradoException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.MateriaNoEncontradaException;

import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteNoEncontradoException;

import mx.edu.cbta.sistemaescolar.horario.domain.exception.DocenteNoDisponibleException;

import mx.edu.cbta.sistemaescolar.horario.dto.HorarioDTO;
import mx.edu.cbta.sistemaescolar.horario.dto.ClaseDTO;

import java.util.List;

/**
 * Servicio de coordinación encargado de validar la integridad y disponibilidad
 * de recursos (docentes y aulas) antes de la creación de horarios y clases.
 */
public interface CoordinadorClasesService {

    /**
     * Verifica si un docente tiene colisiones de horario en el ciclo escolar activo.
     * Compara los nuevos horarios propuestos contra las clases semestrales y
     * actividades paraescolares ya registradas.
     * * @param docenteId Identificador único del docente.
     * @param nuevosHorarios Lista de bloques de tiempo a validar.
     * @throws DocenteNoDisponibleException Si existe un empalme con otra clase o actividad.
     * @throws DocenteNoEncontradoException Si el ID del docente no es válido.
     * @throws CicloEscolarNoEncontradoException Si no hay un ciclo escolar marcado como activo.
     */
    void verificarDisponibilidadDocente(Long docenteId, List<HorarioDTO> nuevosHorarios)
            throws DocenteNoDisponibleException,
            ParaescolarNoEncontradaException,
            MateriaNoEncontradaException,
            DocenteNoEncontradoException,
            CicloEscolarNoEncontradoException;

    /**
     * Verifica si un aula física está libre en los horarios solicitados.
     * Valida que no existan clases semestrales o paraescolares ocupando el espacio
     * en el ciclo escolar vigente.
     * * @param aulaId Identificador del aula (salón, laboratorio, etc.).
     * @param nuevosHorarios Bloques de tiempo a verificar.
     * @throws DocenteNoDisponibleException Si el aula ya está ocupada en ese horario.
     */
    void verificarDisponibilidadAula(Long aulaId, List<HorarioDTO> nuevosHorarios)
            throws DocenteNoDisponibleException,
            ParaescolarNoEncontradaException,
            MateriaNoEncontradaException,
            DocenteNoEncontradoException,
            CicloEscolarNoEncontradoException;

    /**
     * Valida los requisitos para crear una clase semestral individual.
     * Toma el horario único de la clase y verifica la disponibilidad tanto del
     * docente como del aula asignada.
     * * @param claseDTO DTO que contiene el docenteId, aulaId y el objeto horario.
     * @throws DocenteNoDisponibleException Si el docente o el aula están ocupados.
     */
    void validarCreacionClaseSemestral(ClaseDTO claseDTO)
            throws DocenteNoDisponibleException,
            ParaescolarNoEncontradaException,
            MateriaNoEncontradaException,
            DocenteNoEncontradoException,
            CicloEscolarNoEncontradoException;

    /**
     * Realiza una validación masiva para una lista de clases semestrales.
     * Útil al procesar la creación de un grupo completo donde se envían
     * múltiples materias con sus respectivos docentes y horarios.
     * * @param listaClasesDTO Colección de clases a validar una por una.
     * @throws DocenteNoDisponibleException Si alguna de las clases presenta un conflicto.
     */
    void validarCreacionClasesSemestrales(List<ClaseDTO> listaClasesDTO)
            throws DocenteNoDisponibleException,
            ParaescolarNoEncontradaException,
            MateriaNoEncontradaException,
            DocenteNoEncontradoException,
            CicloEscolarNoEncontradoException;
}