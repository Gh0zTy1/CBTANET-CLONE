package mx.edu.cbta.sistemaescolar.estructura.service;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CicloEscolarNoEncontradoException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CrearCicloEscolarException;
import mx.edu.cbta.sistemaescolar.estructura.dto.CicloEscolarDTO;

/**
 * Interfaz de servicio para la administración de Ciclos Escolares.
 * Controla la vigencia y creación de los periodos de tiempo académicos.
 */
public interface CicloEscolarService {

    /**
     * Obtiene el periodo escolar marcado actualmente como activo.
     * @return {@link CicloEscolarDTO} que representa el ciclo vigente.
     * @throws CicloEscolarNoEncontradoException Si no hay ningún ciclo activo definido.
     */
    CicloEscolarDTO obtenerCicloEscolarActivo() throws CicloEscolarNoEncontradoException;

    /**
     * Recupera un ciclo escolar específico por su ID.
     * @param id Identificador único del ciclo.
     * @return {@link CicloEscolarDTO} correspondiente al ID.
     * @throws CicloEscolarNoEncontradoException Si el identificador no existe.
     */
    CicloEscolarDTO obtenerCicloEscolarPorId(Long id) throws CicloEscolarNoEncontradoException;

    /**
     * Crea un nuevo periodo o ciclo escolar en el sistema.
     * @param nuevoCicloDTO Datos del nuevo ciclo escolar.
     * @return {@link CicloEscolarDTO} persistido.
     * @throws CrearCicloEscolarException Si los rangos de fechas o datos son inconsistentes.
     */
    CicloEscolarDTO crearCicloEscolar(CicloEscolarDTO nuevoCicloDTO) throws CrearCicloEscolarException;
}