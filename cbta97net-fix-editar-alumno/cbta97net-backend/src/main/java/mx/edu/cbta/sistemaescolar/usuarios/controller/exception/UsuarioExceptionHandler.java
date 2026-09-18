package mx.edu.cbta.sistemaescolar.usuarios.controller.exception;

import lombok.extern.slf4j.Slf4j;
import mx.edu.cbta.sistemaescolar.usuarios.domain.exception.*;

import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "mx.edu.cbta.sistemaescolar.usuarios.controller")
public class UsuarioExceptionHandler {

    @ExceptionHandler(UsuarioException.class)
    public ResponseEntity<Map<String, String>> handleAdministradorException(UsuarioException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Petición Inválida");
        body.put("message", ex.getMessage());
        log.error("error", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioNoEncontradoException(UsuarioNoEncontradoException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Usuario No Encontrado");
        body.put("message", ex.getMessage());
        log.error("error", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, String>> handleAdministradorException(CredencialesInvalidasException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Credenciales Inválidas");
        body.put("message", ex.getMessage());
        log.error("error", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(RolesException.class)
    public ResponseEntity<Map<String, String>> handleRolesException(RolesException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Petición Inválida");
        body.put("message", ex.getMessage());
        log.error("error", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(PermisosException.class)
    public ResponseEntity<Map<String, String>> handlePermisosException(PermisosException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Petición Inválida");
        body.put("message", ex.getMessage());
        log.error("error", ex);
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
        log.error("error", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body); // 403 Forbidden
    }

    @ExceptionHandler(RegistrarUsuarioException.class)
    public ResponseEntity<Map<String, String>> handleAdministradorException(RegistrarUsuarioException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        body.put("message", ex.getMessage());
        log.error("error", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> body = new HashMap<>();
        log.error(ex.getMessage());
        body.put("error", "Error Desconocido");
        body.put("error", "Ocurrió un error en el servidor. Intente de nuevo.");
        log.error("error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}