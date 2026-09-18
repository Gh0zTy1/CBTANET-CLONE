package mx.edu.cbta.sistemaescolar.alumnado.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AlumnoNoEncontradoException extends RuntimeException {
    public AlumnoNoEncontradoException(String message) {
        super(message);
    }
}