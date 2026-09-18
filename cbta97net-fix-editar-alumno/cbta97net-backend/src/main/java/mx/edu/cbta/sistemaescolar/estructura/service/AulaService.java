package mx.edu.cbta.sistemaescolar.estructura.service;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.AulaNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.AulaDuplicadaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.RegistrarAulaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.EliminarAulaException;
import mx.edu.cbta.sistemaescolar.estructura.dto.AulaDTO;

import java.util.List;

/**
 * Interfaz de servicio que define las operaciones de negocio para la gestión de Aulas.
 * Proporciona métodos para el mantenimiento (CRUD) y consulta de espacios físicos
 * del plantel académico.
 */
public interface AulaService {

    /**
     * Busca un aula específica mediante su identificador único numérico.
     * * @param idAula El identificador único de la base de datos.
     * @return {@link AulaDTO} con los datos del aula encontrada.
     * @throws AulaNoEncontradaException Si no existe un registro con el ID proporcionado.
     */
    AulaDTO obtenerAulaPorId(Long idAula) throws AulaNoEncontradaException;

    /**
     * Recupera la información de un aula utilizando su clave alfanumérica única.
     * * @param clave La clave identificadora del aula (ej. "A-101").
     * @return {@link AulaDTO} con los datos correspondientes.
     * @throws AulaNoEncontradaException Si la clave no coincide con ningún registro.
     */
    AulaDTO obtenerAulaPorClave(String clave) throws AulaNoEncontradaException;

    /**
     * Registra una nueva aula en el sistema.
     * Realiza validaciones de formato (sin espacios en la clave) y unicidad.
     * * @param aula Objeto DTO con la información de la nueva aula.
     * @return {@link AulaDTO} El registro persistido con su ID generado.
     * @throws RegistrarAulaException Si los datos tienen un formato inválido.
     * @throws AulaDuplicadaException Si la clave o el ID ya existen en el sistema.
     */
    AulaDTO registrarAula(AulaDTO aula) throws RegistrarAulaException, AulaDuplicadaException;

    /**
     * Recupera el catálogo completo de aulas registradas en el plantel.
     * * @return List de {@link AulaDTO}, o una lista vacía si no hay registros.
     */
    List<AulaDTO> obtenerTodasLasAulas();

    /**
     * Elimina de forma lógica o física un aula mediante su ID.
     * * @param aulaId El identificador del aula a remover.
     * @throws AulaNoEncontradaException Si el ID no existe.
     * @throws EliminarAulaException Si el aula tiene dependencias (horarios/grupos) que impiden su borrado.
     */
    void eliminarAulaPorId(Long aulaId) throws AulaNoEncontradaException, EliminarAulaException;

    /**
     * Elimina un aula utilizando su clave única.
     * Requiere que el repositorio soporte operaciones transaccionales para campos no-ID.
     * * @param clave La clave del aula a eliminar.
     * @throws AulaNoEncontradaException Si la clave no está registrada.
     * @throws EliminarAulaException Si ocurre un error de integridad referencial.
     */
    void eliminarAulaPorClave(String clave) throws AulaNoEncontradaException, EliminarAulaException;
}
