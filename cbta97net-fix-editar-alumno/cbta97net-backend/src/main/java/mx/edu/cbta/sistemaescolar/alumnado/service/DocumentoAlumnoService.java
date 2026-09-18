package mx.edu.cbta.sistemaescolar.alumnado.service;

import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.AlumnoNoEncontradoException;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.DocumentoAlumnoNoEncontradoException;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.DocumentoAlumnoException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio utilizado para almacenar documentos relacionados con el alumno, tales como acta de nacimiento,
 * certificado de secundaria, CURP, asi como su foto escolar.
 */
public interface DocumentoAlumnoService {

    /**
     * Guarda el acta de nacimiento de un alumno con la matricula especificada.
     * @param matricula Matricula del alumno.
     * @param documento Documento de Acta de Nacimiento.
     * @throws DocumentoAlumnoException si no se puede guardar el documento en el sistema.
     */
    void guardarActaNacimiento(String matricula, MultipartFile documento) throws DocumentoAlumnoException, AlumnoNoEncontradoException;

    /**
     * Obtiene el acta de nacimiento de un alumno.
     * @param matricula Matricula del alumno.
     * @throws DocumentoAlumnoNoEncontradoException Si no se puede obtener el documento guardado en el sistema.
     */
    byte[] obtenerActaNacimiento(String matricula) throws DocumentoAlumnoNoEncontradoException, AlumnoNoEncontradoException;

    /**
     * Guarda la CURP de un alumno con la matrícula especificada.
     * @param matricula Matrícula del alumno.
     * @param documento Documento de la CURP (PDF o Imagen).
     * @throws DocumentoAlumnoException si no se puede guardar el documento.
     */
    void guardarCurp(String matricula, MultipartFile documento) throws DocumentoAlumnoException, AlumnoNoEncontradoException;

    /**
     * Obtiene la CURP de un alumno.
     * @param matricula Matrícula del alumno.
     * @throws DocumentoAlumnoNoEncontradoException Si no se encuentra el documento.
     */
    byte[] obtenerCurp(String matricula) throws DocumentoAlumnoNoEncontradoException, AlumnoNoEncontradoException;

    /**
     * Guarda el certificado de secundaria de un alumno.
     * @param matricula Matrícula del alumno.
     * @param documento Documento del Certificado.
     * @throws DocumentoAlumnoException si hay un error al guardar.
     */
    void guardarCertificadoSecundaria(String matricula, MultipartFile documento) throws DocumentoAlumnoException, AlumnoNoEncontradoException;

    /**
     * Obtiene el certificado de secundaria de un alumno.
     * @param matricula Matrícula del alumno.
     * @throws DocumentoAlumnoNoEncontradoException Si el certificado no existe.
     */
    byte[] obtenerCertificadoSecundaria(String matricula) throws DocumentoAlumnoNoEncontradoException, AlumnoNoEncontradoException;

    /**
     * Guarda la fotografía oficial del alumno.
     * @param matricula Matrícula del alumno.
     * @param documento Archivo de imagen de la fotografía.
     * @throws DocumentoAlumnoException si no se puede procesar la imagen.
     */
    void guardarFotoEscolar(String matricula, MultipartFile documento) throws DocumentoAlumnoException, AlumnoNoEncontradoException;

    /**
     * Obtiene la fotografía escolar del alumno.
     * @param matricula Matrícula del alumno.
     * @throws DocumentoAlumnoNoEncontradoException Si la fotografía no existe.
     */
    byte[] obtenerFotoEscolar(String matricula) throws DocumentoAlumnoNoEncontradoException, AlumnoNoEncontradoException;

    /**
     * Obtiene el nombre del archivo de Acta de Nacimiento del Alumno almacenado en el servidor SFTP
     * en caso de que exista.
     * @param matricula Matrícula del Alumno registrado en el sistema.
     * @return Nombre del archivo de Acta de Nacimiento o null si no existe.
     * @throws AlumnoNoEncontradoException En caso de que no se encuentre al Alumno con la matrícula dada.
     */
    String verificarExistenciaActaNacimiento(String matricula) throws AlumnoNoEncontradoException;

    /**
     * Obtiene el nombre del archivo de la CURP del Alumno almacenado en el servidor SFTP
     * en caso de que exista.
     * @param matricula Matrícula del Alumno registrado en el sistema.
     * @return Nombre del archivo de la CURP o null si no existe.
     * @throws AlumnoNoEncontradoException En caso de que no se encuentre al Alumno con la matrícula dada.
     */
    String verificarExistenciaCurp(String matricula) throws AlumnoNoEncontradoException;

    /**
     * Obtiene el nombre del archivo del Certificado de Secundaria del Alumno almacenado en el servidor SFTP
     * en caso de que exista.
     * @param matricula Matrícula del Alumno registrado en el sistema.
     * @return Nombre del archivo del Certificado de Secundaria o null si no existe.
     * @throws AlumnoNoEncontradoException En caso de que no se encuentre al Alumno con la matrícula dada.
     */
    String verificarExistenciaCertificado(String matricula) throws AlumnoNoEncontradoException;


    String verificarExistenciaFotoEscolar(String matricula) throws AlumnoNoEncontradoException;
}