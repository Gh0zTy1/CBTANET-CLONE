package mx.edu.cbta.sistemaescolar.estructura.util;

import mx.edu.cbta.sistemaescolar.estructura.domain.model.Materia;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Interfaz util para obtencion de las materias presentes en el archivo provisto por
 * SISEEMS (Subsecretaría de Educación Media Superior).
 */
public interface LectorMateriasSISEEMS {

    /**
     * Extrae las materias existentes en el archivo de información proveniente de la
     * plataforma SISEEMS (Subsecretaría de Educación Media Superior).
     *
     * @param archivo Archivo en formato Hoja de Calculo (Excel).
     * @return Lista de materias encontradas.
     * @throws IOException Si ocurre un error al leer el archivo cargado.
     */
    List<Materia> obtenerMaterias(InputStream archivo) throws IOException;
}
