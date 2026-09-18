package mx.edu.cbta.sistemaescolar.alumnado.service.impl;

import mx.edu.cbta.sistemaescolar.alumnado.service.AlumnoService;
import mx.edu.cbta.sistemaescolar.alumnado.service.DocumentoAlumnoService;
import mx.edu.cbta.sistemaescolar.alumnado.service.SFTPService;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.AlumnoNoEncontradoException;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.DocumentoAlumnoNoEncontradoException;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.DocumentoAlumnoException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentoAlumnoServiceImpl implements DocumentoAlumnoService {

    private final SFTPService sftpService;
    private final AlumnoService alumnoService;

    /**
     * Constructor para facilitacion de pruebas unitarias.
     * @param sftpService Servicio puente para la conexion al servidor SFTP.
     * @param alumnoService Servicio para acceso y modificacion de la informacion de alumnos.
     */
    public DocumentoAlumnoServiceImpl(SFTPService sftpService, AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
        this.sftpService = sftpService;
    }

    /**
     * Guarda el acta de nacimiento de un alumno con la matricula especificada.
     * @param matricula Matricula del alumno.
     * @param documento Documento de Acta de Nacimiento.
     * @throws DocumentoAlumnoException si no se puede guardar el documento en el sistema.
     * @throws AlumnoNoEncontradoException si no se encuentra el alumno con la matricula dada.
     */
    @Override
    public void guardarActaNacimiento(String matricula, MultipartFile documento) throws DocumentoAlumnoException, AlumnoNoEncontradoException {

        boolean alumnoExiste = this.alumnoService.obtenerAlumnoPorMatricula(matricula) != null;

        if (!alumnoExiste) {
            throw new AlumnoNoEncontradoException("No se encontró al alumno con la matrícula: '%s'".formatted(matricula));
        }

        String rutaArchivo = "uploads/alumnos/%s/Acta_Nacimiento_%s.pdf".formatted(matricula, matricula);

        try {

            sftpService.guardarOReemplazar(documento.getInputStream(),rutaArchivo);

        }catch (Exception e) {
            throw new DocumentoAlumnoException(e.getMessage());
        }
    }

    /**
     * Obtiene el acta de nacimiento de un alumno.
     * @param matricula Matricula del alumno.
     * @throws DocumentoAlumnoNoEncontradoException Si no se puede obtener el documento guardado en el sistema.
     * @throws AlumnoNoEncontradoException si no se encuentra el alumno con la matricula dada.
     */
    @Override
    public byte[] obtenerActaNacimiento(String matricula) throws DocumentoAlumnoNoEncontradoException, AlumnoNoEncontradoException {

        boolean alumnoExiste = this.alumnoService.obtenerAlumnoPorMatricula(matricula) != null;

        if (!alumnoExiste) {
            throw new AlumnoNoEncontradoException("No se encontró al alumno con la matrícula: '%s'".formatted(matricula));
        }

        String rutaArchivo = "uploads/alumnos/%s/Acta_Nacimiento_%s.pdf".formatted(matricula,matricula);

        return sftpService.obtenerArchivo(rutaArchivo);
    }

    /**
     * Guarda la CURP de un alumno con la matrícula especificada.
     * @param matricula Matrícula del alumno.
     * @param documento Documento de la CURP (PDF o Imagen).
     * @throws DocumentoAlumnoException si no se puede guardar el documento.
     * @throws AlumnoNoEncontradoException si no se encuentra el alumno con la matricula dada.
     */
    @Override
    public void guardarCurp(String matricula, MultipartFile documento) throws DocumentoAlumnoException, AlumnoNoEncontradoException {

        boolean alumnoExiste = this.alumnoService.obtenerAlumnoPorMatricula(matricula) != null;

        if (!alumnoExiste) {
            throw new AlumnoNoEncontradoException("No se encontró al alumno con la matrícula: '%s'".formatted(matricula));
        }

        String rutaArchivo = "uploads/alumnos/%s/CURP_%s.pdf".formatted(matricula,matricula);

        try {

            sftpService.guardarOReemplazar(documento.getInputStream(),rutaArchivo);

        }catch (Exception e) {
            throw new DocumentoAlumnoException(e.getMessage());
        }
    }

    /**
     * Obtiene la CURP de un alumno.
     * @param matricula Matrícula del alumno.
     * @throws DocumentoAlumnoNoEncontradoException Si no se encuentra el documento.
     * @throws AlumnoNoEncontradoException si no se encuentra el alumno con la matricula dada.
     */
    @Override
    public byte[] obtenerCurp(String matricula) throws DocumentoAlumnoNoEncontradoException, AlumnoNoEncontradoException {

        boolean alumnoExiste = this.alumnoService.obtenerAlumnoPorMatricula(matricula) != null;

        if (!alumnoExiste) {
            throw new AlumnoNoEncontradoException("No se encontró al alumno con la matrícula: '%s'".formatted(matricula));
        }

        String rutaArchivo = "uploads/alumnos/%s/CURP_%s.pdf".formatted(matricula,matricula);

        return sftpService.obtenerArchivo(rutaArchivo);
    }

    /**
     * Guarda el certificado de secundaria de un alumno.
     * @param matricula Matrícula del alumno.
     * @param documento Documento del Certificado.
     * @throws DocumentoAlumnoException si hay un error al guardar.
     * @throws AlumnoNoEncontradoException si no se encuentra el alumno con la matricula dada.
     */
    @Override
    public void guardarCertificadoSecundaria(String matricula, MultipartFile documento) throws DocumentoAlumnoException, AlumnoNoEncontradoException {

        boolean alumnoExiste = this.alumnoService.obtenerAlumnoPorMatricula(matricula) != null;

        if (!alumnoExiste) {
            throw new AlumnoNoEncontradoException("No se encontró al alumno con la matrícula: '%s'".formatted(matricula));
        }

        String rutaArchivo = "uploads/alumnos/%s/CertificadoSecundaria_%s.pdf".formatted(matricula,matricula);

        try {

            sftpService.guardarOReemplazar(documento.getInputStream(),rutaArchivo);

        }catch (Exception e) {
            throw new DocumentoAlumnoException(e.getMessage());
        }
    }

    /**
     * Obtiene el certificado de secundaria de un alumno.
     * @param matricula Matrícula del alumno.
     * @throws DocumentoAlumnoNoEncontradoException Si el certificado no existe.
     * @throws AlumnoNoEncontradoException si no se encuentra el alumno con la matricula dada.
     */
    @Override
    public byte[] obtenerCertificadoSecundaria(String matricula) throws DocumentoAlumnoNoEncontradoException, AlumnoNoEncontradoException {

        boolean alumnoExiste = this.alumnoService.obtenerAlumnoPorMatricula(matricula) != null;

        if (!alumnoExiste) {
            throw new AlumnoNoEncontradoException("No se encontró al alumno con la matrícula: '%s'".formatted(matricula));
        }

        String rutaArchivo = "uploads/alumnos/%s/CertificadoSecundaria_%s.pdf".formatted(matricula,matricula);

        return sftpService.obtenerArchivo(rutaArchivo);
    }

    /**
     * Guarda la fotografía oficial del alumno.
     * @param matricula Matrícula del alumno.
     * @param documento Archivo de imagen de la fotografía.
     * @throws DocumentoAlumnoException si no se puede procesar la imagen.
     * @throws AlumnoNoEncontradoException si no se encuentra el alumno con la matricula dada.
     */
    @Override
    public void guardarFotoEscolar(String matricula, MultipartFile documento) throws DocumentoAlumnoException, AlumnoNoEncontradoException {

        boolean alumnoExiste = this.alumnoService.obtenerAlumnoPorMatricula(matricula) != null;

        if (!alumnoExiste) {
            throw new AlumnoNoEncontradoException("No se encontró al alumno con la matrícula: '%s'".formatted(matricula));
        }

        String rutaArchivo = "uploads/alumnos/%s/FotoEscolar_%s.png".formatted(matricula,matricula);

        try {

            sftpService.guardarOReemplazar(documento.getInputStream(),rutaArchivo);

        }catch (Exception e) {
            throw new DocumentoAlumnoException(e.getMessage());
        }
    }

    /**
     * Obtiene la fotografía escolar del alumno.
     * @param matricula Matrícula del alumno.
     * @throws DocumentoAlumnoNoEncontradoException Si la fotografía no existe.
     * @throws AlumnoNoEncontradoException si no se encuentra el alumno con la matricula dada.
     */
    @Override
    public byte[] obtenerFotoEscolar(String matricula) throws DocumentoAlumnoNoEncontradoException, AlumnoNoEncontradoException {

        boolean alumnoExiste = this.alumnoService.obtenerAlumnoPorMatricula(matricula) != null;

        if (!alumnoExiste) {
            throw new AlumnoNoEncontradoException("No se encontró al alumno con la matrícula: '%s'".formatted(matricula));
        }

        String rutaArchivo = "uploads/alumnos/%s/FotoEscolar_%s.png".formatted(matricula,matricula);

        if (!sftpService.existeArchivo(rutaArchivo)) {
            throw new DocumentoAlumnoNoEncontradoException("El alumno con matrícula %s no cuenta con fotografía registrada.".formatted(matricula));
        }

        return sftpService.obtenerArchivo(rutaArchivo);
    }

    @Override
    public String verificarExistenciaActaNacimiento(String matricula) throws AlumnoNoEncontradoException {
        validarExistenciaAlumno(matricula);
        String nombreArchivo = "Acta_Nacimiento_%s.pdf".formatted(matricula);
        String rutaArchivo = "uploads/alumnos/%s/%s".formatted(matricula, nombreArchivo);

        return sftpService.existeArchivo(rutaArchivo) ? nombreArchivo : null;
    }

    @Override
    public String verificarExistenciaCurp(String matricula) throws AlumnoNoEncontradoException {
        validarExistenciaAlumno(matricula);
        String nombreArchivo = "CURP_%s.pdf".formatted(matricula);
        String rutaArchivo = "uploads/alumnos/%s/%s".formatted(matricula, nombreArchivo);

        return sftpService.existeArchivo(rutaArchivo) ? nombreArchivo : null;
    }

    @Override
    public String verificarExistenciaCertificado(String matricula) throws AlumnoNoEncontradoException {
        validarExistenciaAlumno(matricula);
        String nombreArchivo = "CertificadoSecundaria_%s.pdf".formatted(matricula);
        String rutaArchivo = "uploads/alumnos/%s/%s".formatted(matricula, nombreArchivo);

        return sftpService.existeArchivo(rutaArchivo) ? nombreArchivo : null;
    }

    @Override
    public String verificarExistenciaFotoEscolar(String matricula) throws AlumnoNoEncontradoException {
        validarExistenciaAlumno(matricula);
        String nombreArchivo = "FotoEscolar_%s.png".formatted(matricula);
        String rutaArchivo = "uploads/alumnos/%s/%s".formatted(matricula, nombreArchivo);

        return sftpService.existeArchivo(rutaArchivo) ? nombreArchivo : null;
    }

    /**
     * Método privado auxiliar para evitar repetición de código de validación
     */
    private void validarExistenciaAlumno(String matricula) throws AlumnoNoEncontradoException {
        boolean alumnoExiste = this.alumnoService.obtenerAlumnoPorMatricula(matricula) != null;
        if (!alumnoExiste) {
            throw new AlumnoNoEncontradoException("No se encontró al alumno con la matrícula: '%s'".formatted(matricula));
        }
    }
}