package mx.gob.controlescolar.acceso.aplicacion;

import mx.gob.controlescolar.comun.dominio.AuditoriaEvento;
import mx.gob.controlescolar.comun.persistencia.AuditoriaRepositorio;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepositorio auditoria;

    public void registrar(Long institucionId, String accion, String detalle) {
        auditoria.save(new AuditoriaEvento(institucionId, accion, detalle));
    }
}
