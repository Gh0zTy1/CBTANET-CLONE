package mx.edu.cbta.sistemaescolar.alumnado.controller.exception;

import lombok.extern.slf4j.Slf4j;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.ActualizarAlumnoException;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.EliminarAlumnoException;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.RegistrarAlumnoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.AlumnoNoEncontradoException;

@Slf4j
@RestControllerAdvice(basePackages = "mx.edu.cbta.sistemaescolar.alumnado.controller")
public class AlumnoExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        // Obtenemos el mensaje del primer error de validación encontrado
        // Esto evita que el bucle sobreescriba la llave "error" innecesariamente
        String mensajeError = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        errores.put("error", mensajeError);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    @ExceptionHandler(AlumnoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleAlumnoNoEncontrado(AlumnoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ActualizarAlumnoException.class)
    public ResponseEntity<Map<String, String>> handleActualizarAlumno(ActualizarAlumnoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EliminarAlumnoException.class)
    public ResponseEntity<Map<String, String>> handleEliminarAlumno(EliminarAlumnoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RegistrarAlumnoException.class)
    public ResponseEntity<Map<String, String>> handleRegistrarAlumno(RegistrarAlumnoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error de E/S al procesar la solicitud: " + ex.getMessage()));
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Argumento inválido: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Ocurrió un error inesperado."));
    }
}