package mx.edu.cbta.sistemaescolar.personal.controller.exception;

import lombok.extern.slf4j.Slf4j;
import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteNoDisponibleException;
import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteNoEncontradoException;
import mx.edu.cbta.sistemaescolar.personal.domain.exception.RegistrarDocenteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "mx.edu.cbta.sistemaescolar.personal.controller")
public class PersonalExceptionHandler {

    @ExceptionHandler(DocenteNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleDocenteNoEncontradoException(DocenteNoEncontradoException ex) {
        log.warn("docente no encontrado: {}", ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", "Acceso denegado");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DocenteNoDisponibleException.class)
    public ResponseEntity<Map<String, String>> handleDocenteNoDisponibleoException(DocenteNoDisponibleException ex) {
        log.warn("docente no disponible: {}", ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", "No Disponible");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(RegistrarDocenteException.class)
    public ResponseEntity<Map<String, String>> handleRegistrarDocenteException(RegistrarDocenteException ex) {
        log.warn("### registro de docentes: {}", ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", "Acceso denegado");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> body = new HashMap<>();
        log.error(ex.toString());
        body.put("error", "Ocurrió un error inesperado en el servidor.");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}