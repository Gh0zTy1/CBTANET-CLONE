package mx.edu.cbta.sistemaescolar.estructura.service;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ImportarMateriasSISEEMSException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.SemestreMateriaNoValidoException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.MateriaNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.MateriaDuplicadaException;
import mx.edu.cbta.sistemaescolar.estructura.dto.MateriaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de Materias/Asignaturas.
 * Administra el currículo académico y las relaciones jerárquicas entre materias.
 */
public interface MateriaService {

    /**
     * Consulta una materia específica por su identificador.
     * @param id ID de la materia.
     * @return {@link MateriaDTO} con los datos de la asignatura.
     * @throws MateriaNoEncontradaException Si el registro no existe.
     */
    MateriaDTO obtenerMateriaPorId(Long id) throws MateriaNoEncontradaException;

    /**
     * Registra una nueva asignatura en el catálogo curricular.
     * @param materiaDto Datos de la asignatura a registrar.
     * @return {@link MateriaDTO} persistido.
     * @throws SemestreMateriaNoValidoException Si el semestre asignado es fuera de rango.
     * @throws MateriaDuplicadaException Si la clave o nombre ya existe.
     */
    MateriaDTO registrarMateria(MateriaDTO materiaDto) throws SemestreMateriaNoValidoException, MateriaDuplicadaException;

    /**
     * Obtiene todas las materias sin filtros aplicados.
     * @return Lista total de {@link MateriaDTO}.
     */
    List<MateriaDTO> obtenerTodasLasMaterias();

    /**
     * Filtra las materias pertenecientes a un Área Propedéutica específica.
     * @param areaPropedeuticaId ID del área de especialización.
     * @return Lista de materias del área indicada.
     */
    List<MateriaDTO> obtenerTodasPorAreaPropedeutica(Long areaPropedeuticaId);

    /**
     * Obtiene las materias asociadas a una Carrera Técnica.
     * @param carreraTecnicaId ID de la carrera técnica.
     * @return Lista de materias curriculares de la carrera.
     */
    List<MateriaDTO> obtenerMateriasPorCarrera(Long carreraTecnicaId);

    /**
     * Consulta las materias que se imparten en un semestre específico.
     * @param semestre Número del semestre (1-6).
     * @return Lista de materias del nivel solicitado.
     */
    List<MateriaDTO> obtenerMateriasPorSemestre(int semestre);

    /**
     * Filtra materias por semestre y por carrera técnica asociada.
     * @param semestre Nivel académico solicitado.
     * @param carreraTecnicaId Carrera técnica de interés.
     * @return Lista de materias que cumplen ambos criterios.
     */
    List<MateriaDTO> obtenerMateriasPorSemestreYCarrera(int semestre, Long carreraTecnicaId);

    /**
     * Realiza una búsqueda exhaustiva por semestre, carrera y área propedéutica.
     * @param semestre Semestre a consultar.
     * @param carreraTecnicaId Identificador de la carrera.
     * @param areaPropedeuticaId Identificador del área.
     * @return Lista de materias filtrada por los tres criterios.
     */
    List<MateriaDTO> obtenerMateriasPorSemestreYCarreraYArea(int semestre, Long carreraTecnicaId, Long areaPropedeuticaId);

    /**
     * Importa las materias presentes en el archivo provisto por SISEEMS y las almacena
     * en el sistema.
     * Las materias ya existentes en el sistema se omiten al realizar el registro
     * masivo.
     * @param archivoSISEEMS Archivo de tipo Hoja de Calculo (Excel) de SISEEMS.
     * @throws ImportarMateriasSISEEMSException Si ocurre un error al intentar obtener los archivos del archivo.
     */
    void importarMateriasSISEEMS(MultipartFile archivoSISEEMS) throws ImportarMateriasSISEEMSException;
}