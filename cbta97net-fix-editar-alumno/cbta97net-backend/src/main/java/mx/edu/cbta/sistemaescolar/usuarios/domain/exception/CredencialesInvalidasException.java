package mx.edu.cbta.sistemaescolar.usuarios.domain.exception;

public class CredencialesInvalidasException extends Exception {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
