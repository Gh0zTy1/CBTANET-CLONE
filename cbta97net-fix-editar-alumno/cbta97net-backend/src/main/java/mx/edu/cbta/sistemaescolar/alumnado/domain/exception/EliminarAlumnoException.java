package mx.edu.cbta.sistemaescolar.alumnado.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EliminarAlumnoException extends RuntimeException {
    public EliminarAlumnoException(String message) {
        super(message);
    }
}