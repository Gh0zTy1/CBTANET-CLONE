package mx.edu.cbta.sistemaescolar.grupos.service;

import mx.edu.cbta.sistemaescolar.grupos.domain.exception.*;
import mx.edu.cbta.sistemaescolar.grupos.dto.GrupoDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio encargado de gestionar el ciclo de vida de los grupos semestrales.
 * Proporciona métodos para la administración académica, incluyendo la creación,
 * actualización, eliminación y consulta de grupos.
 */
public interface GrupoService {

    /**
     * Registra un nuevo grupo en el sistema.
     * * @param grupoDTO Objeto con la información del grupo y sus clases asociadas.
     * @return El {@link GrupoDTO} persistido con su ID generado.
     * @throws GrupoException Si ocurre un error de validación o lógica de negocio.
     * @throws RegistrarGrupoException Si ocurre una excepcion en el guardado del grupo.
     * @throws GrupoYaExistenteException si ya existe un grupo con la letra especificada.
     */
    GrupoDTO registrarGrupo(GrupoDTO grupoDTO) throws GrupoException, RegistrarGrupoException, GrupoYaExistenteException;

    /**
     * Actualiza la información de un grupo existente.
     * * @param grupoDTO DTO con los datos actualizados.
     * @throws GrupoException Si los datos son inválidos.
     * @throws GrupoNoEncontradoException Si el ID del grupo no existe en la base de datos.
     */
    void actualizarGrupo(GrupoDTO grupoDTO) throws GrupoException, GrupoNoEncontradoException;

    /**
     * Elimina un grupo del sistema mediante su identificador.
     * * @param idGrupo Identificador único del grupo a eliminar.
     * @throws GrupoException Si el grupo tiene dependencias que impiden su borrado.
     * @throws GrupoNoEncontradoException Si el ID proporcionado no corresponde a ningún grupo.
     */
    void eliminarGrupo(Long idGrupo) throws EliminarGrupoSemestralException, GrupoNoEncontradoException;

    /**
     * Recupera una lista paginada de todos los grupos registrados.
     * @param pageable Limitador de paginacion.
     * * @return Un objeto {@link Page} que contiene los grupos en formato DTO.
     */
    Page<GrupoDTO> obtenerGrupos(Pageable pageable);

    /**
     * Busca y retorna la información detallada de un grupo específico.
     * @param idGrupo Identificador único del grupo.
     * @return El {@link GrupoDTO} correspondiente al ID.
     * @throws GrupoException Si ocurre un error durante la recuperación del registro.
     * @throws GrupoNoEncontradoException Cuando no se encontro el grupo.
     */
    GrupoDTO obtenerGrupoPorId(Long idGrupo) throws GrupoException, GrupoNoEncontradoException;

    /**
     * Obtiene todos los grupos asociados a un ciclo escolar específico.
     * * @param idCicloEscolar Identificador del ciclo escolar (ej. 2024-2025).
     * @return Una lista de {@link GrupoDTO} pertenecientes al ciclo consultado.
     */
    List<GrupoDTO> obtenerGruposPorCicloEscolar(Long idCicloEscolar);

    List<GrupoDTO> obtenerGruposActivos() throws GrupoException;
}