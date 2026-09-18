package mx.edu.cbta.sistemaescolar.estructura.util;

import mx.edu.cbta.sistemaescolar.estructura.domain.model.CarreraTecnica;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Interfaz util para acceder a las carreras tecnicas presentes en el archivo provisto
 * por SISEEMS (Subsecretaría de Educación Media Superior).
 */
public interface LectorCarrerasTecnicasSISEEMS {

    /**
     * Extrae las carreras tecnicas existentes en el archivo de información proveniente
     * de la plataforma SISEEMS (Subsecretaría de Educación Media Superior).
     *
     * @param archivo Archivo en formato Hoja de Calculo (Excel).
     * @return Lista de carreras técnicas encontradas.
     * @throws IOException Si ocurre un error al leer el archivo cargado.
     */
    List<CarreraTecnica> obtenerCarrerasTecnicas(InputStream archivo) throws IOException;
}