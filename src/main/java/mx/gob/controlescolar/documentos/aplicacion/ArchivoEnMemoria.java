package mx.gob.controlescolar.documentos.aplicacion;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Profile("test")
public class ArchivoEnMemoria implements ArchivoEscolar {

    private final AtomicLong secuencia = new AtomicLong();
    private final Map<String, byte[]> archivos = new ConcurrentHashMap<>();

    @Override
    public String guardar(Long institucionId, byte[] contenido) {
        String id = institucionId + "-" + secuencia.incrementAndGet();
        archivos.put(id, contenido);
        return id;
    }

    @Override
    public byte[] leer(String archivoId) {
        return archivos.get(archivoId);
    }
}
