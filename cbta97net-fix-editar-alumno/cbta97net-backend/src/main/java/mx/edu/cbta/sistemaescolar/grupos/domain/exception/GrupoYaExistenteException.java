package mx.edu.cbta.sistemaescolar.grupos.domain.exception;

public class GrupoYaExistenteException extends Exception {
    public GrupoYaExistenteException(String message) {
        super(message);
    }
}