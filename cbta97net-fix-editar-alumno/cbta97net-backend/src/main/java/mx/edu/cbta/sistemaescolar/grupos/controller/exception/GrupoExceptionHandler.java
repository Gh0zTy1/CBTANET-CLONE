package mx.edu.cbta.sistemaescolar.grupos.controller.exception;

import lombok.extern.slf4j.Slf4j;
import mx.edu.cbta.sistemaescolar.grupos.domain.exception.GrupoNoEncontradoException;
import mx.edu.cbta.sistemaescolar.grupos.domain.exception.GrupoYaExistenteException;
import mx.edu.cbta.sistemaescolar.grupos.domain.exception.RegistrarGrupoException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "mx.edu.cbta.sistemaescolar.grupos.controller")
public class GrupoExceptionHandler {

    /**
     * Maneja el caso cuando un grupo no existe.
     * Respuesta: 404 Not Found.
     */
    @ExceptionHandler(GrupoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleGrupoNoEncontrado(GrupoNoEncontradoException ex) {
        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("error", "Grupo No Encontrado");
        respuesta.put("message", ex.getMessage());

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja conflictos de duplicidad (ej. letra de grupo repetida).
     * Respuesta: 409 Conflict.
     */
    @ExceptionHandler(GrupoYaExistenteException.class)
    public ResponseEntity<Map<String, String>> handleGrupoYaExistente(GrupoYaExistenteException ex) {
        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("error", "Grupo Ya Existente");
        respuesta.put("message", ex.getMessage());

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(respuesta, HttpStatus.CONFLICT);
    }

    /**
     * Maneja errores generales de lógica de negocio y fallos en el registro.
     * Respuesta: 400 Bad Request.
     */
    @ExceptionHandler(RegistrarGrupoException.class)
    public ResponseEntity<Map<String, String>> handleErroresNegocio(RegistrarGrupoException ex) {
        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("error", "Registrar Grupo");
        respuesta.put("message", ex.getMessage());

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Error Interno");
        body.put("message", "Ocurrió un error inesperado en el servidor.");

        log.error(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}