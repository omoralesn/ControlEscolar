package mx.gob.controlescolar.comun.aplicacion;

public class NegocioException extends RuntimeException {

    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
