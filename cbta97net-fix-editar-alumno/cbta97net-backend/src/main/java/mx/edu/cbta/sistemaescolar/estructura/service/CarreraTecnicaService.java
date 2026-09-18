package mx.edu.cbta.sistemaescolar.estructura.service;

import mx.edu.cbta.sistemaescolar.estructura.dto.CarreraTecnicaDTO;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de Carreras Técnicas.
 * Define las operaciones para administrar los programas educativos técnicos.
 */
public interface CarreraTecnicaService {

    /**
     * Busca una carrera técnica por su identificador único.
     * @param idCarrera ID de la carrera a consultar.
     * @return {@link CarreraTecnicaDTO} con la información detallada.
     * @throws CarreraTecnicaNoEncontradaException Si el identificador no es válido.
     */
    CarreraTecnicaDTO obtenerCarreraTecnicaPorId(Long idCarrera) throws CarreraTecnicaNoEncontradaException;

    /**
     * Recupera la lista completa de carreras técnicas activas.
     * @return Lista de objetos {@link CarreraTecnicaDTO}.
     */
    List<CarreraTecnicaDTO> obtenerCarrerasTodas();

    /**
     * Registra un nuevo programa de carrera técnica.
     * @param carreraTecnicaDTO Datos del programa a registrar.
     * @return {@link CarreraTecnicaDTO} con los datos persistidos.
     * @throws RegistrarCarreraTecnicaException Si existe un error en los datos de entrada.
     * @throws CarreraTecnicaDuplicadaException Si el nombre de la carrera ya existe.
     */
    CarreraTecnicaDTO registrarCarreraTecnica(CarreraTecnicaDTO carreraTecnicaDTO)
            throws RegistrarCarreraTecnicaException, CarreraTecnicaDuplicadaException;

    /**
     * Remueve una carrera técnica del catálogo mediante su ID.
     * @param carreraId Identificador de la carrera a eliminar.
     * @throws CarreraTecnicaNoEncontradaException Si el registro no existe.
     * @throws EliminarCarreraTecnicaException Si la carrera tiene materias o alumnos vinculados.
     */
    void eliminarCarreraTecnicaPorId(Long carreraId)
            throws CarreraTecnicaNoEncontradaException, EliminarCarreraTecnicaException;

    /**
     * Importa las carreras tecnicas presentes en el archivo provisto como parametro.
     * El archivo dado debe ser de tipo Excel (hoja de calculo) provisto por SISEEMS.
     * Si la carrera tecnica ya existe se omite en el proceso de insercion masiva.
     * @param archivoSISEEMS Archivo de SISEEMS tipo Excel (Hoja de Calculo).
     * @throws ImportarCarrerasTecnicasSISEEMSException Si ocurre un error al intentar obtener los archivos del archivo.
     */
    void importarCarrerasTecnicasSISEEMS(MultipartFile archivoSISEEMS) throws ImportarCarrerasTecnicasSISEEMSException;
}