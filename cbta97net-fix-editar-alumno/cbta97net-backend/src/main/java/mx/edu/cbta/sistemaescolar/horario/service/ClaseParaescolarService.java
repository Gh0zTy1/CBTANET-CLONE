package mx.edu.cbta.sistemaescolar.horario.service;

import mx.edu.cbta.sistemaescolar.horario.dto.ClaseParaescolarDTO;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.ClaseGrupoParaescolarNoEncontradoException;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.EliminarClasesParaescolaresException;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.RegistrarClasesParaescolarException;

import java.util.List;

/**
 * Servicio especializado en la gestión de clases para actividades paraescolares.
 * Maneja el registro de horarios para actividades complementarias y su relación con docentes.
 */
public interface ClaseParaescolarService {

    /**
     * Recupera las actividades paraescolares asignadas a un grupo de alumnos.
     * @param idGrupo Identificador único del grupo.
     * @return Lista de {@link ClaseParaescolarDTO} asociadas.
     * @throws ClaseGrupoParaescolarNoEncontradoException Si no existen actividades para el grupo.
     */
    List<ClaseParaescolarDTO> obtenerClasesPorGrupo(Long idGrupo) throws ClaseGrupoParaescolarNoEncontradoException;

    /**
     * Registra las actividades paraescolares para un grupo determinado.
     * @param grupoId Identificador del grupo.
     * @param listaClasesDTO Lista de actividades paraescolares a dar de alta.
     * @return Lista de actividades registradas exitosamente.
     * @throws RegistrarClasesParaescolarException Si hay conflictos en el registro.
     */
    List<ClaseParaescolarDTO> registrarClases(Long grupoId, List<ClaseParaescolarDTO> listaClasesDTO)
            throws RegistrarClasesParaescolarException;

    /**
     * Consulta las actividades paraescolares que un docente tiene asignadas actualmente.
     * @param docenteId Identificador del docente.
     * @return Lista de {@link ClaseParaescolarDTO} vigentes.
     */
    List<ClaseParaescolarDTO> obtenerClasesVigentesDocente(Long docenteId);

    /**
     * Elimina de forma masiva todas las clases paraescolares vinculadas a un grupo.
     * @param grupoId Identificador del grupo.
     * @throws EliminarClasesParaescolaresException Si el proceso de borrado falla.
     */
    void eliminarClasesParaescolaresPorGrupo(Long grupoId) throws EliminarClasesParaescolaresException;
}