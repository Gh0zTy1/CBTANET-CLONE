package mx.edu.cbta.sistemaescolar.gruposparaescolares.service;

import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.exception.*;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.dto.AlumnoInscritoParaescolarDTO;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.dto.BajaMasivaAlumnosDTO;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.dto.GrupoParaescolarDTO;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.dto.InscripcionMasivaAlumnosDTO;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ParaescolarNoEncontradaException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Servicio para operaciones relacionadas con los grupos de actividades paraescolares utilizando DTOs.
 */
public interface GrupoParaescolarService {

    /**
     * Obtiene una lista paginada de grupos paraescolares en formato DTO.
     * @param pageable La información de paginación y ordenamiento.
     * @return Una página de objetos GrupoParaescolarDTO.
     */
    Page<GrupoParaescolarDTO> obtenerGruposParaescolares(Pageable pageable);

    /**
     * Obtiene una lista paginada de grupos paraescolares activos (ciclo escolar vigente).
     * @return Una página de objetos GrupoParaescolarDTO.
     */
    List<GrupoParaescolarDTO> obtenerGruposParaescolaresActivos() throws GrupoParaescolarException;

    /**
     * Crea un nuevo grupo para una actividad paraescolar recibiendo un DTO.
     * @param grupoParaescolarDTO Datos del grupo a guardar.
     * @return el grupo paraescolar creado en formato DTO.
     * @throws CrearGrupoParaescolarException cuando ocurra un error al intentar crear el grupo.
     */
    GrupoParaescolarDTO crearGrupo(GrupoParaescolarDTO grupoParaescolarDTO) throws CrearGrupoParaescolarException;

    /**
     * Verifica si un paraescolar tiene alumnos actualmente inscritos.
     *
     * @param idParaescolar el identificador del paraescolar a verificar.
     * @return {@code true} si existen alumnos inscritos, {@code false} en caso contrario.
     * @throws ParaescolarNoEncontradaException si el paraescolar con el ID proporcionado no existe.
     */
    boolean tieneAlumnosInscritosEnParaescolar(Long idParaescolar) throws ParaescolarNoEncontradaException;

    /**
     * Obtiene todos los grupos paraescolares que un docente llevó en un ciclo escolar dado.
     * @param idDocente ID del docente a cargo del grupo.
     * @param idCicloEscolar ID del ciclo escolar.
     * @return Lista de grupos en formato DTO.
     * @throws GrupoParaescolarNoEncontradoException Si no se encuentran registros.
     */
    List<GrupoParaescolarDTO> obtenerGruposParaescolaresPorDocenteYCicloEscolar(Long idDocente, Long idCicloEscolar)
            throws GrupoParaescolarNoEncontradoException;

    /**
     * Obtiene todos los grupos paraescolares que un docente está llevando en el ciclo escolar en curso.
     * @param idDocente ID del docente a cargo del grupo.
     * @return Lista de grupos en formato DTO.
     */
    List<GrupoParaescolarDTO> obtenerGruposParaescolaresActualesPorDocente(Long idDocente);

    /**
     * Inscribe un alumno a un grupo paraescolar específico.
     * @param matricula La matrícula del alumno a inscribir.
     * @param idGrupo El identificador único del grupo paraescolar.
     * @return Un objeto {@code AlumnoInscritoParaescolarDTO} que representa la inscripción exitosa.
     * @throws GrupoParaescolarNoEncontradoException Si el grupo no existe.
     * @throws InscribirAlumnoParaescolarException Si ocurre un error de validación.
     */
    AlumnoInscritoParaescolarDTO inscribirAlumnoAGrupoParaescolar(String matricula, Long idGrupo)
            throws GrupoParaescolarNoEncontradoException, InscribirAlumnoParaescolarException;

    /**
     * Obtiene un grupo paraescolar específico por su ID en formato DTO.
     * @param id Identificador del grupo.
     * @return El DTO del grupo encontrado.
     * @throws GrupoParaescolarNoEncontradoException Si el grupo no existe.
     */
    GrupoParaescolarDTO obtenerGrupoPorId(Long id) throws GrupoParaescolarNoEncontradoException;

    /**
     * Elimina un grupo paraescolar del sistema si este no cuenta con ningun alumno inscrito.
     * @param grupoId
     * @throws GrupoParaescolarNoEncontradoException
     */
    void eliminarGrupoParaescolar(Long grupoId) throws GrupoParaescolarNoEncontradoException;

    /**
     * Efectua la baja de un alumno en un grupo paraescolar activo.
     * @param matricula Matrícula del alumno.
     * @param idGrupo ID del grupo paraescolar.
     * @throws BajaAlumnoParaescolarException Si ocurre un error al intentar efectuar el proceso de la baja.
     * @throws GrupoParaescolarNoEncontradoException Si no se pudo encontrar el grupo paraescolar en el sistema.
     */
    void darDeBajaAlumnoDeGrupoParaescolar(String matricula, Long idGrupo)
            throws BajaAlumnoParaescolarException, GrupoParaescolarNoEncontradoException;

    /**
     * Inscribe a una multitud de alumnos a un grupo paraescolar. La operación se detiene si no ALGUNO de los
     * alumnos no se puede inscribir debido a cualquier razón.
     * @param inscripcionMasivaAlumnosDTO Contiene la información necesaria para efectuar el proceso de inscripción de alumnos.
     * @throws GrupoParaescolarNoEncontradoException En caso de no encontrar el grupo.
     * @throws InscribirAlumnoParaescolarException Si algún alumno no se puede inscribir debido a cualquier razón.
     */
    void inscribirMasivamenteAlumnosAGrupoParaescolar(InscripcionMasivaAlumnosDTO inscripcionMasivaAlumnosDTO)
            throws GrupoParaescolarNoEncontradoException, InscribirAlumnoParaescolarException;

    /**
     * Se da de baja una multitud de alumnos de un grupo paraescolar. La operación se detiene si no ALGUNO de los
     * alumnos no se puede dar de baja debido a cualquier razón.
     * @throws BajaAlumnoParaescolarException En caso de no encontrar el grupo.
     * @throws GrupoParaescolarNoEncontradoException Si algún alumno no se puede dar de baja debido a cualquier razón.
     */
    void darBajaMasivamenteAlumnosDeGrupoParaescolar(BajaMasivaAlumnosDTO bajaMasivaAlumnosDTO)
            throws BajaAlumnoParaescolarException, GrupoParaescolarNoEncontradoException;
}