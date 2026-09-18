package mx.edu.cbta.sistemaescolar.alumnado.controller;

import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.*;
import mx.edu.cbta.sistemaescolar.alumnado.dto.*;
import mx.edu.cbta.sistemaescolar.alumnado.service.DocumentoAlumnoService;
import mx.edu.cbta.sistemaescolar.alumnado.service.AlumnoService;
import mx.edu.cbta.sistemaescolar.alumnado.mapper.AlumnoMapper;
import mx.edu.cbta.sistemaescolar.alumnado.domain.model.Alumno;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alumnos")
public class AlumnoController {

    private final AlumnoService alumnoService;
    private final DocumentoAlumnoService documentoService;
    private final AlumnoMapper alumnoMapper;

    public AlumnoController(AlumnoService alumnoService, DocumentoAlumnoService documentoService, AlumnoMapper alumnoMapper) {
        this.alumnoService = alumnoService;
        this.documentoService = documentoService;
        this.alumnoMapper = alumnoMapper;
    }

    /**
     * Actualiza la información personal y académica de un alumno existente.
     * * @param matricula Matrícula del alumno a actualizar (Path Variable).
     * @param alumnoDTO DTO con los nuevos datos del alumno.
     * @return ResponseEntity con el DTO actualizado.
     */
    @PutMapping("/{matricula}")
    @PreAuthorize("hasRole('ALUMNOS_UPDATE')")
    public ResponseEntity<AlumnoDTO> actualizarAlumno(
            @PathVariable String matricula,
            @RequestBody EditarInformacionAlumnoDTO alumnoDTO
    ) throws ActualizarAlumnoException, AlumnoNoEncontradoException {

        alumnoDTO.setMatricula(matricula);

        System.out.println("### NUEVOS DATOS A ACTUALIZAR: " + this.alumnoMapper.toDTO(alumnoDTO));

        AlumnoDTO alumnoActualizado = alumnoService.actualizarAlumno(this.alumnoMapper.toDTO(alumnoDTO));
        return ResponseEntity.ok(alumnoActualizado);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ALUMNOS_READ')")
    public ResponseEntity<Page<InformacionBasicaAlumnoDTO>> obtenerAlumnosPaginadosPorBusqueda(
            @PageableDefault(page = 0, size = 10, sort = "nombre") Pageable pageable,
            @RequestParam(name = "credenciales", required = false, defaultValue = "") String credenciales
    ) {
        Page<InformacionBasicaAlumnoDTO> pagina = this.alumnoService.obtenerAlumnosPorCredenciales(credenciales, pageable);
        return ResponseEntity.ok(pagina);
    }

    // TODO: Eliminar este e intercambiarlo por /buscar cuando se pase a produccion...
    @GetMapping
    @PreAuthorize("hasRole('ALUMNOS_READ')")
    public ResponseEntity<Page<AlumnoDTO>> listarGruposPaginados(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable)
    {
        Page<AlumnoDTO> alumnosPageDTO = this.alumnoService.obtenerAlumnosTodos(pageable);
        return ResponseEntity.ok(alumnosPageDTO);
    }

    @DeleteMapping("/{matricula}")
    @PreAuthorize("hasRole('ALUMNOS_DELETE')")
    public ResponseEntity<Map<String, String>> eliminarAlumnoPorMatricula(@PathVariable("matricula") String matricula) {
        this.alumnoService.eliminarAlumno(matricula);

        Map<String, String> body = new HashMap<>();

        body.put("message", "Se ha eliminado al alumno correctamente.");

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{matricula}")
    @PreAuthorize("hasRole('ALUMNOS_READ')")
    public ResponseEntity<AlumnoDTO> obtenerAlumnoPorMatricula(@PathVariable String matricula) {
        AlumnoDTO alumnoDTO = this.alumnoService.obtenerAlumnoPorMatricula(matricula);

        try {
            alumnoDTO.setDocumentoActaNacimiento(this.documentoService.verificarExistenciaActaNacimiento(matricula));
        } catch (AlumnoNoEncontradoException ex) {}

        try {
            alumnoDTO.setDocumentoCertificadoSecundaria(this.documentoService.verificarExistenciaCertificado(matricula));
        } catch (AlumnoNoEncontradoException ex) {}

        try {
            alumnoDTO.setDocumentoCURP(this.documentoService.verificarExistenciaCurp(matricula));
        } catch (AlumnoNoEncontradoException ex) {}

        try {
            String nombre = this.documentoService.verificarExistenciaFotoEscolar(matricula);
            alumnoDTO.setDocumentoFotoEscolar(nombre);
            System.out.println("NOMBRE FOTO WE: " + nombre);
        } catch (AlumnoNoEncontradoException ex) {}

        return ResponseEntity.ok(alumnoDTO);
    }

    /**
     * Registra un nuevo alumno en el sistema.
     * <p>
     * Este endpoint recibe la información personal y académica básica de un alumno,
     * la transforma de DTO a entidad y persiste el registro en la base de datos.
     * </p>
     *
     * @param alumnoDTO Objeto que contiene los datos de registro del alumno.
     * @return {@link ResponseEntity} con el objeto {@link Alumno} creado y estatus 201 (Created).
     */
    @PostMapping
    @PreAuthorize("hasRole('ALUMNOS_CREATE')")
    public ResponseEntity<AlumnoDTO> registrarAlumno(@RequestBody AlumnoDTO alumnoDTO) {
        AlumnoDTO alumnoRegistradoDTO = alumnoService.registrarAlumno(alumnoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(alumnoRegistradoDTO);
    }

    /**
     * Almacena el acta de nacimiento digitalizada de un alumno específico.
     * <p>
     * El archivo se recibe mediante un formulario multipart/form-data y se asocia
     * a la matrícula proporcionada en la ruta.
     * </p>
     *
     * @param matricula         Matrícula única del alumno al que pertenece el documento.
     * @param actaNacimientoDTO DTO que encapsula el archivo {@link MultipartFile}.
     * @return {@link ResponseEntity} con un mensaje de confirmación de éxito.
     * @throws DocumentoAlumnoException Si ocurre un error de E/S o validación al guardar el archivo.
     */
    @PostMapping("/{matricula}/documentos/acta-nacimiento")
    @PreAuthorize("hasRole('ALUMNOS_UPDATE')")
    public ResponseEntity<?> guardarActaNacimiento(
            @PathVariable("matricula") String matricula,
            @ModelAttribute GuardarDocumentoAlumnoDTO actaNacimientoDTO) throws DocumentoAlumnoException {

        this.documentoService.guardarActaNacimiento(matricula, actaNacimientoDTO.getDocumento());
        Map<String, Object> body = new HashMap<>();
        body.put("message", "El acta de nacimiento se guardó correctamente.");
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * Almacena el documento CURP (Clave Única de Registro de Población) del alumno.
     *
     * @param matricula Matrícula única del alumno.
     * @param curpDTO   DTO que contiene el archivo del CURP.
     * @return {@link ResponseEntity} indicando el éxito de la operación.
     * @throws DocumentoAlumnoException Si el proceso de almacenamiento falla.
     */
    @PostMapping("/{matricula}/documentos/curp")
    @PreAuthorize("hasRole('ALUMNOS_UPDATE')")
    public ResponseEntity<?> guardarCurp(
            @PathVariable String matricula,
            @ModelAttribute GuardarDocumentoAlumnoDTO curpDTO) throws DocumentoAlumnoException {

        this.documentoService.guardarCurp(matricula, curpDTO.getDocumento());
        return ResponseEntity.ok(Map.of("message", "El documento del CURP se guardó correctamente."));
    }

    /**
     * Almacena el certificado de estudios de nivel secundaria del alumno.
     *
     * @param matricula      Matrícula única del alumno.
     * @param certificadoDTO DTO que contiene el archivo del certificado.
     * @return {@link ResponseEntity} con el mensaje de confirmación.
     * @throws DocumentoAlumnoException Si no se logra persistir el archivo correctamente.
     */
    @PostMapping("/{matricula}/documentos/certificado-secundaria")
    @PreAuthorize("hasRole('ALUMNOS_UPDATE')")
    public ResponseEntity<?> guardarCertificado(
            @PathVariable String matricula,
            @ModelAttribute GuardarDocumentoAlumnoDTO certificadoDTO) throws DocumentoAlumnoException {

        this.documentoService.guardarCertificadoSecundaria(matricula, certificadoDTO.getDocumento());
        return ResponseEntity.ok(Map.of("message", "El certificado de secundaria se guardó correctamente."));
    }

    /**
     * Actualiza la fotografía escolar oficial del alumno.
     * <p>
     * Se recomienda que este archivo sea en formato de imagen (JPEG/PNG).
     * </p>
     *
     * @param matricula      Matrícula única del alumno.
     * @param fotoEscolarDTO DTO que contiene la imagen de la fotografía.
     * @return {@link ResponseEntity} con estatus 200 (OK).
     * @throws DocumentoAlumnoException Si existe un problema al procesar o guardar la imagen.
     */
    @PostMapping("/{matricula}/documentos/foto")
    @PreAuthorize("hasRole('ALUMNOS_UPDATE')")
    public ResponseEntity<?> guardarFoto(
            @PathVariable String matricula,
            @ModelAttribute GuardarDocumentoAlumnoDTO fotoEscolarDTO) throws DocumentoAlumnoException {

        this.documentoService.guardarFotoEscolar(matricula, fotoEscolarDTO.getDocumento());
        return ResponseEntity.ok(Map.of("message", "La foto escolar del alumno se guardó correctamente."));
    }

    /*-------------------Obtener Archivos-----------------------*/


    /**
     *
     * @param matricula Matrícula única del alumno.
     * @return archivo pdf especifico
     * @throws DocumentoAlumnoException Si existe un problema al procesar o guardar la imagen
     */
    @GetMapping("/{matricula}/documentos/acta-nacimiento")
    @PreAuthorize("hasRole('ALUMNOS_READ')")
    public ResponseEntity<?> obtenerActaNacimiento(@PathVariable("matricula") String matricula) throws DocumentoAlumnoException {

        byte[] archivoEnByte = this.documentoService.obtenerActaNacimiento(matricula);

        return construirRespuestaPdf(archivoEnByte, "acta_nacimiento.pdf");
    }

    /**
     *
     * @param matricula Matrícula única del alumno.
     * @return archivo pdf especifico
     * @throws DocumentoAlumnoException Si existe un problema al procesar o guardar la imagen
     */
    @GetMapping("/{matricula}/documentos/curp")
    @PreAuthorize("hasRole('ALUMNOS_READ')")
    public ResponseEntity<?> obtenerCurp(@PathVariable String matricula) throws DocumentoAlumnoException {

        byte[] archivoEnByte = this.documentoService.obtenerCurp(matricula);

        return construirRespuestaPdf(archivoEnByte, "curp.pdf");
    }


    /**
     *
     * @param matricula Matrícula única del alumno.
     * @return archivo pdf especifico
     * @throws DocumentoAlumnoException Si existe un problema al procesar o guardar la imagen
     */
    @GetMapping("/{matricula}/documentos/certificado-secundaria")
    @PreAuthorize("hasRole('ALUMNOS_READ')")
    public ResponseEntity<?> obtenerCertificado( @PathVariable String matricula) throws DocumentoAlumnoException {

        byte[] archivoEnByte = this.documentoService.obtenerCertificadoSecundaria(matricula);

        return construirRespuestaPdf(archivoEnByte, "certificado_secundaria.pdf");
    }


    /// ///////////////////////[Verificacion de existencia de Archivos de Alumno]/////////////////////////////

    /**
     * Verifica la existencia del acta de nacimiento y devuelve su nombre si existe.
     * @param matricula Matrícula única del alumno.
     * @return Map con el nombre del archivo o null.
     * @throws AlumnoNoEncontradoException Si no se encuentra el alumno.
     */
    @GetMapping("/{matricula}/documentos/acta-nacimiento/verificar")
    @PreAuthorize("hasRole('ALUMNOS_READ')")
    public ResponseEntity<Map<String, String>> verificarActaNacimiento(@PathVariable String matricula)
            throws AlumnoNoEncontradoException {

        String nombre = this.documentoService.verificarExistenciaActaNacimiento(matricula);
        return ResponseEntity.ok(Map.of("documento", nombre != null ? nombre : ""));
    }

    /**
     * Verifica la existencia de la CURP y devuelve su nombre si existe.
     * @param matricula Matrícula única del alumno.
     * @return Map con el nombre del archivo o null.
     * @throws AlumnoNoEncontradoException Si no se encuentra el alumno.
     */
    @GetMapping("/{matricula}/documentos/curp/verificar")
    @PreAuthorize("hasRole('ALUMNOS_READ')")
    public ResponseEntity<Map<String, String>> verificarCurp(@PathVariable String matricula)
            throws AlumnoNoEncontradoException {

        String nombre = this.documentoService.verificarExistenciaCurp(matricula);
        return ResponseEntity.ok(Map.of("documento", nombre != null ? nombre : ""));
    }

    /**
     * Verifica la existencia del certificado de secundaria y devuelve su nombre si existe.
     * @param matricula Matrícula única del alumno.
     * @return Map con el nombre del archivo o null.
     * @throws AlumnoNoEncontradoException Si no se encuentra el alumno.
     */
    @GetMapping("/{matricula}/documentos/certificado-secundaria/verificar")
    @PreAuthorize("hasRole('ALUMNOS_READ')")
    public ResponseEntity<Map<String, String>> verificarCertificado(@PathVariable String matricula)
            throws AlumnoNoEncontradoException {

        String nombre = this.documentoService.verificarExistenciaCertificado(matricula);
        return ResponseEntity.ok(Map.of("documento", nombre != null ? nombre : ""));
    }

    /**
     *
     * @param matricula Matrícula única del alumno.
     * @return archivo pdf especifico
     * @throws DocumentoAlumnoException Si existe un problema al procesar o guardar la imagen
     */
    @GetMapping("/{matricula}/documentos/foto")
    @PreAuthorize("hasRole('ALUMNOS_READ')")
    public ResponseEntity<?> obtenerFoto(@PathVariable String matricula) throws DocumentoAlumnoException, DocumentoAlumnoNoEncontradoException {

        byte[] archivoEnByte = this.documentoService.obtenerFotoEscolar(matricula);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"foto_" + matricula + ".png\"")
                .body(archivoEnByte);
    }

    private ResponseEntity<Resource> construirRespuestaPdf(byte[] datos, String nombreArchivo) {
        ByteArrayResource resource = new ByteArrayResource(datos);

        return ResponseEntity.ok()
                // "attachment" para forzar la descarga
                // "inline" para ver en navegador
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(datos.length)
                .body(resource);
    }

    /*-----------------------Importar Alumnos----------------------------*/
    @PostMapping("/importar")
    @PreAuthorize("hasRole('ALUMNOS_CREATE')")
    public ResponseEntity<?> importar(@ModelAttribute ImportarAlumnosDTO dto) throws ImportarAlumnosException {
        System.out.println("inció de importación");
        this.alumnoService.importarAlumnos(dto.getDocumento());

        Map<String, String> body = new HashMap<>();
        body.put("message", "Los alumnos se han importado correctamente.");

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}