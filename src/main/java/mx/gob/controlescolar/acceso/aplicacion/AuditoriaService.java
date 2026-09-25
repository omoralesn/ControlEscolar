package mx.gob.controlescolar.acceso.aplicacion;

import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.comun.dominio.AuditoriaEvento;
import mx.gob.controlescolar.comun.persistencia.AuditoriaRepositorio;
import mx.gob.controlescolar.comun.web.SesionActual;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

/** Bitácora de acceso y de capturas escolares (calificaciones, listas y documentos). */
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepositorio auditoria;
    private final SesionActual sesion;

    public void registrar(Long institucionId, String accion, String detalle) {
        registrar(institucionId, accion, detalle, null, null);
    }

    public void registrar(Long institucionId, String accion, String detalle, String afectadoLogin,
                          String perfilNombre) {
        Usuario actor = sesion.usuario();
        Long actorId = actor == null ? null : actor.getId();
        String actorLogin = actor == null ? null : actor.getUsername();
        auditoria.save(new AuditoriaEvento(institucionId, accion, detalle, actorId, actorLogin, afectadoLogin,
                perfilNombre));
    }

    public List<AuditoriaEvento> deLaEscuela(Long institucionId) {
        return auditoria.findByInstitucionIdOrderByOcurridoEnDesc(institucionId);
    }
}