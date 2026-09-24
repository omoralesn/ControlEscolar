package mx.gob.controlescolar.documentos.aplicacion;

public interface ArchivoEscolar {
    String guardar(Long institucionId, byte[] contenido);

    byte[] leer(String archivoId);
}
