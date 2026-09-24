package mx.gob.controlescolar.comun.web;

import mx.gob.controlescolar.acceso.dominio.Usuario;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SesionActual {

    public Usuario usuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario usuario)) {
            return null;
        }
        return usuario;
    }

    public Long institucionId() {
        Usuario usuario = usuario();
        if (usuario == null || !usuario.esEscuela()) {
            throw new NegocioException("Esta pantalla es de una escuela");
        }
        return usuario.getInstitucion().getId();
    }
}
