package mx.edu.cbta.sistemaescolar.estructura.service;

import mx.edu.cbta.sistemaescolar.estructura.dto.ActividadParaescolarDTO;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ParaescolarNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ModificarParaescolarException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.EliminarParaescolarException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CrearParaescolarException;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.ActividadParaescolar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio para la gestión de actividades paraescolares.
 */
public interface ActividadParaescolarService {

    /**
     * Obtiene la lista completa de actividades paraescolares registradas.
     *
     * @return una lista de objetos {@link ActividadParaescolar}.
     */
    List<ActividadParaescolarDTO> obtenerParaescolares();

    /**
     * Crea una nueva actividad paraescolar.
     *
     * @param paraescolarDTO el objeto que contiene los datos de la nueva actividad.
     * @return la actividad paraescolar creada.
     */
    ActividadParaescolarDTO crearActividadParaescolar(ActividadParaescolarDTO paraescolarDTO) throws CrearParaescolarException;

    /**
     * Modifica una actividad paraescolar existente.
     *
     * @param id el identificador de la actividad paraescolar a modificar.
     * @param paraescolarModificadoDTO el objeto con los datos actualizados.
     * @return la actividad paraescolar modificada.
     */
    ActividadParaescolarDTO modificarParaescolar(Long id, ActividadParaescolarDTO paraescolarModificadoDTO) throws ModificarParaescolarException, ParaescolarNoEncontradaException;

    /**
     * Elimina una actividad paraescolar mediante su identificador.
     *
     * @param id el identificador de la actividad paraescolar a eliminar.
     */
    void eliminarParaescolar(Long id) throws ParaescolarNoEncontradaException, EliminarParaescolarException;

    /**
     * Obtiene una actividad paraescolar por su identificador.
     *
     * @param id el identificador de la actividad paraescolar.
     * @return la actividad paraescolar encontrada.
     */
    ActividadParaescolarDTO obtenerParaescolarPorId(Long id) throws ParaescolarNoEncontradaException;

    /**
     * Regresa una pagina configurada de actividades paraescolares.
     * @param nombre Nombre o titulo de actividades paraescolares.
     * @return
     */
    Page<ActividadParaescolarDTO> obtenerActividadesParaescolaresPorNombre(String nombre, Pageable pageable);
}
