package mx.edu.cbta.sistemaescolar.gruposparaescolares.controller.exception;

import lombok.extern.slf4j.Slf4j;

import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.AlumnoNoEncontradoException;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.exception.BajaAlumnoParaescolarException;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.exception.CrearGrupoParaescolarException;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.exception.GrupoParaescolarNoEncontradoException;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.exception.InscribirAlumnoParaescolarException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "mx.edu.cbta.sistemaescolar.gruposparaescolares.controller")
public class GrupoParaescolarExceptionHandler {

    @ExceptionHandler(AlumnoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleAlumnoNoEncontradoException(AlumnoNoEncontradoException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Alumno no encontrado");
        body.put("message", ex.getMessage());
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InscribirAlumnoParaescolarException.class)
    public ResponseEntity<Map<String, Object>> handleInscribirAlumnoParaescolarException(InscribirAlumnoParaescolarException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "No se pudo inscribir al alumno");
        body.put("message", ex.getMessage());
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BajaAlumnoParaescolarException.class)
    public ResponseEntity<Map<String, Object>> handleBajaAlumnoParaescolarException(BajaAlumnoParaescolarException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "No se pudo dar de baja al alumno");
        body.put("message", ex.getMessage());
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CrearGrupoParaescolarException.class)
    public ResponseEntity<Map<String, Object>> manejarCrearGrupoException(CrearGrupoParaescolarException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Conflicto en la creación del grupo paraescolar");
        body.put("message", ex.getMessage());
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(GrupoParaescolarNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleGrupoNoEncontrado(GrupoParaescolarNoEncontradoException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Grupo no encontrado");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> body = new HashMap<>();
        log.error(ex.getMessage(), ex);
        body.put("error", "Ocurrió un error inesperado en el servidor.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}