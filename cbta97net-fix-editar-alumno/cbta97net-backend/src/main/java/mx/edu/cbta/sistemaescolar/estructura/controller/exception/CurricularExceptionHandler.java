package mx.edu.cbta.sistemaescolar.estructura.controller.exception;

import lombok.extern.slf4j.Slf4j;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "mx.edu.cbta.sistemaescolar.estructura.controller")
public class CurricularExceptionHandler {

    @ExceptionHandler(ImportarCarrerasTecnicasSISEEMSException.class)
    public ResponseEntity<Map<String, Object>> handleImportarCarrerasTecnicasSISEEMS(ImportarCarrerasTecnicasSISEEMSException ex) {
        log.error("Error al importar carreras técnicas desde SISEEMS: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Error en importación de carreras");
        body.put("message", ex.getMessage());
        // Se usa BAD_REQUEST si el error suele ser por el formato del archivo o datos inválidos
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ImportarMateriasSISEEMSException.class)
    public ResponseEntity<Map<String, Object>> handleImportarMateriasSISEEMS(ImportarMateriasSISEEMSException ex) {
        log.error("Error al importar materias desde SISEEMS: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Error en importación de materias");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(IOException ex) {
        log.error("Error de lectura de archivo: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Error de archivo");
        body.put("message", "No se pudo procesar el archivo Excel. Verifique que no esté dañado o abierto.");
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(AulaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleAulaNoEncontrada(AulaNoEncontradaException ex) {
        log.warn("Aula no encontrada: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Aula no encontrada");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(AulaDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> handleAulaDuplicada(AulaDuplicadaException ex) {
        log.warn("Conflicto: El aula ya existe: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Aula duplicada");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(RegistrarAulaException.class)
    public ResponseEntity<Map<String, Object>> handleRegistrarAula(RegistrarAulaException ex) {
        log.error("Error al registrar aula: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Error de registro");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(EliminarAulaException.class)
    public ResponseEntity<Map<String, Object>> handleEliminarAula(EliminarAulaException ex) {
        log.error("Conflicto al eliminar aula: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Error de eliminación");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(SemestreMateriaNoValidoException.class)
    public ResponseEntity<Map<String, String>> handleSemestreNoValido(SemestreMateriaNoValidoException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Semestre no permitido");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AreaPropedeuticaDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> handleAreaPropedeuticaDuplicada(AreaPropedeuticaDuplicadaException ex) {
        log.warn("Conflicto: El área propedéutica ya existe: {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("error", "Área duplicada");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MateriaDuplicadaException.class)
    public ResponseEntity<Map<String, String>> handleMateriaDuplicada(MateriaDuplicadaException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Conflicto de datos");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(CicloEscolarException.class)
    public ResponseEntity<Map<String, String>> handleCicloEscolarExceptions(CicloEscolarException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Operación No Valida");
        body.put("message", ex.getMessage());
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(CicloEscolarNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleCicloEscolarNoEncontradoExceptions(CicloEscolarNoEncontradoException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Recurso No Encontrado");
        body.put("message", ex.getMessage());
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(CrearCicloEscolarException.class)
    public ResponseEntity<Map<String, String>> handleCrearCicloEscolarExceptions(CrearCicloEscolarException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Operación No Valida");
        body.put("message", ex.getMessage());
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MateriaNoEncontradaException.class)
    public ResponseEntity<Object> handleMateriaNoEncontrada(MateriaNoEncontradaException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Materia No Encontrada");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegistrarCarreraTecnicaException.class)
    public ResponseEntity<Map<String, Object>> handleRegistrarCarrera(RegistrarCarreraTecnicaException ex) {
        log.warn("Error al registrar carrera técnica: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Error de registro");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(CarreraTecnicaDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> handleCarreraDuplicada(CarreraTecnicaDuplicadaException ex) {
        log.warn("Conflicto: Carrera técnica ya existe: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Carrera duplicada");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(CarreraTecnicaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleCarreraNoEncontrada(CarreraTecnicaNoEncontradaException ex) {
        log.warn("Carrera técnica no encontrada: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "No encontrado");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }



    @ExceptionHandler(AreaPropedeuticaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleAreaPropedeeuticaNoEncontrada(AreaPropedeuticaNoEncontradaException ex) {
        log.warn("Área propedéutica no encontrada: {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Área propedéutica no encontrada");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(EliminarAreaPropedeuticaException.class)
    public ResponseEntity<Map<String, Object>> handleEliminarAreaPropedeutica(EliminarAreaPropedeuticaException ex) {
        log.error("Error al intentar eliminar el área propedéutica: {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Error al eliminar");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(EliminarCarreraTecnicaException.class)
    public ResponseEntity<Map<String, Object>> handleEliminarCarreraTecnica(EliminarCarreraTecnicaException ex) {
        log.error("Error al eliminar carrera técnica: {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Error de eliminación");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> body = new HashMap<>();
        log.error(ex.getMessage(), ex);
        body.put("error", "Ocurrió un error inesperado en el servidor.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(EliminarParaescolarException.class)
    public ResponseEntity<Map<String, String>> handleBadRequestExceptions(EliminarParaescolarException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Solicitud inválida");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body); // 400 Bad Request
    }


    /**
     * Maneja errores de falta de permisos (HTTP 403 Forbidden).
     * Ocurre cuando el token es válido pero el usuario no tiene el rol necesario.
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.warn("Intento de acceso no autorizado: {}", ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", "Acceso denegado");
        body.put("message", "No tienes los permisos suficientes para realizar esta acción.");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body); // 403 Forbidden
    }

    /**
     * Maneja las excepciones relacionadas con la lógica de negocio que resultan
     * en una solicitud inválida (HTTP 400).
     * Esto incluye errores como duplicidad de nombres al crear o modificar.
     */
    @ExceptionHandler({
            CrearParaescolarException.class,
            ModificarParaescolarException.class
    })
    public ResponseEntity<Map<String, String>> handleBadRequestExceptions(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body); // 400 Bad Request
    }

    /**
     * Maneja la excepción cuando una actividad paraescolar no es encontrada (HTTP 404).
     */
    @ExceptionHandler(ParaescolarNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(ParaescolarNoEncontradaException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Recurso no encontrado");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body); // 404 Not Found
    }
}