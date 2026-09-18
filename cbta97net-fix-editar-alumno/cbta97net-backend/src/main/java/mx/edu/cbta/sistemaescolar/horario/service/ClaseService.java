package mx.edu.cbta.sistemaescolar.horario.service;

import mx.edu.cbta.sistemaescolar.horario.domain.exception.GrupoNoEncontradoException;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.ClaseGrupoNoEncontradoException;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.ClaseNoEncontradaException;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.EliminarClasesGrupoException;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.RegistrarClasesException;
import mx.edu.cbta.sistemaescolar.horario.dto.ClaseDTO;

import java.util.List;

/**
 * Servicio encargado de la gestión de clases semestrales ordinarias.
 * Permite la administración de horarios de materias por grupo y la verificación de carga académica de docentes.
 */
public interface ClaseService {

    /**
     * Recupera todas las clases asociadas a un grupo específico.
     * @param idGrupo Identificador único del grupo.
     * @return Lista de {@link ClaseDTO} con la información de materias, docentes y horarios.
     * @throws ClaseGrupoNoEncontradoException Si el grupo no tiene clases registradas.
     */
    List<ClaseDTO> obtenerClasesPorGrupo(Long idGrupo) throws ClaseGrupoNoEncontradoException;

    /**
     * Realiza el registro masivo de clases para un grupo.
     * @param grupoId Identificador único del grupo al que se le asignarán las clases.
     * @param listaClasesDTO Colección de clases con su respectiva información de horario.
     * @return Lista de {@link ClaseDTO} persistidas con éxito.
     * @throws RegistrarClasesException Si ocurre un error durante el proceso de guardado o validación.
     */
    List<ClaseDTO> registrarClases(Long grupoId, List<ClaseDTO> listaClasesDTO) throws RegistrarClasesException;

    /**
     * Obtiene la programación de clases asignadas a un aula física determinada.
     * @param aulaId Identificador del aula o salón.
     * @return Lista de clases programadas en dicho espacio.
     * @throws ClaseNoEncontradaException Si no se encuentran registros para el aula.
     */
    List<ClaseDTO> obtenerClasesPorAula(Long aulaId) throws ClaseNoEncontradaException;

    /**
     * Consulta la carga horaria vigente asignada a un docente.
     * @param docenteId Identificador del docente.
     * @return Lista de clases que el docente imparte actualmente.
     */
    List<ClaseDTO> obtenerClasesVigentesDocente(Long docenteId);

    void eliminarClasesPorGrupo(Long grupoId) throws EliminarClasesGrupoException, GrupoNoEncontradoException;
}