package mx.edu.cbta.sistemaescolar.estructura.service;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.AreaPropedeuticaNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.AreaPropedeuticaDuplicadaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.EliminarAreaPropedeuticaException;
import mx.edu.cbta.sistemaescolar.estructura.dto.AreaPropedeuticaDTO;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de Áreas Propedéuticas.
 * Define las operaciones para administrar las especialidades de bachillerato.
 */
public interface AreaPropedeuticaService {

    /**
     * Registra una nueva área propedéutica en el sistema.
     *
     * @param areaPropedeutica Objeto DTO con la información del área a registrar.
     * @return El {@link AreaPropedeuticaDTO} creado con su ID generado.
     * @throws AreaPropedeuticaDuplicadaException Si ya existe un área con el mismo nombre o clave.
     */
    AreaPropedeuticaDTO registrarAreaPropedeutica(AreaPropedeuticaDTO areaPropedeutica) throws AreaPropedeuticaDuplicadaException;

    /**
     * Busca y recupera la información de un área propedéutica mediante su identificador único.
     *
     * @param id Identificador único del área propedéutica.
     * @return El {@link AreaPropedeuticaDTO} correspondiente al ID proporcionado.
     * @throws AreaPropedeuticaNoEncontradaException Si no se encuentra ningún registro con el ID especificado.
     */
    AreaPropedeuticaDTO obtenerAreaPropedeuticaPorId(Long id) throws AreaPropedeuticaNoEncontradaException;

    /**
     * Recupera la lista completa de todas las áreas propedéuticas registradas en el sistema.
     *
     * @return Una {@link List} de {@link AreaPropedeuticaDTO}. Si no hay registros, devuelve una lista vacía.
     */
    List<AreaPropedeuticaDTO> obtenerAreasPropedeuticas();

    /**
     * Elimina un área propedéutica del sistema por su identificador.
     *
     * @param areaId Identificador único del área que se desea eliminar.
     * @throws AreaPropedeuticaNoEncontradaException Si el ID proporcionado no existe en la base de datos.
     * @throws EliminarAreaPropedeuticaException Si el área no puede ser eliminada debido a restricciones
     * de integridad referencial (por ejemplo, si tiene alumnos o materias vinculadas).
     */
    void eliminarAreaPorId(Long areaId) throws AreaPropedeuticaNoEncontradaException, EliminarAreaPropedeuticaException;
}