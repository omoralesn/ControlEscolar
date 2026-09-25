package mx.gob.controlescolar.comun.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.acceso.dominio.Usuario;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.RequiredArgsConstructor;

import java.util.Set;

@ControllerAdvice
@RequiredArgsConstructor
public class MenuAdvice {

    private final ObjectProvider<SesionActual> sesion;
    private final ObjectProvider<ModuloGuardia> modulos;

    @ModelAttribute
    public void menu(Model model, HttpServletRequest request) {
        publicarAvisos(model, request);
        SesionActual actual = sesion.getIfAvailable();
        Usuario usuario = actual == null ? null : actual.usuario();
        ModuloGuardia guardia = modulos.getIfAvailable();
        Set<Modulo> habilitados = Set.of();
        if (usuario != null && usuario.esEscuela() && guardia != null) {
            habilitados = guardia.activos(usuario.getInstitucion().getId());
        }
        model.addAttribute("menuModulos", habilitados);
        model.addAttribute("esPlataforma", usuario != null && usuario.esPlataforma());
        model.addAttribute("esSuper", usuario != null && usuario.esSuper());
        model.addAttribute("sesionEscuela", usuario != null && usuario.esEscuela());
        model.addAttribute("esTutor", usuario != null && usuario.esTutor());
        model.addAttribute("autenticado", usuario != null);
        model.addAttribute("sesionNombre", usuario == null ? null : usuario.getNombre());
        model.addAttribute("escuelaNombre", usuario != null && usuario.getInstitucion() != null
                ? usuario.getInstitucion().getNombre() : null);
        model.addAttribute("rutaActual", rutaActual());
    }

    private static String rutaActual() {
        var atributos = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (atributos instanceof org.springframework.web.context.request.ServletRequestAttributes servlet) {
            String uri = servlet.getRequest().getRequestURI();
            String contexto = servlet.getRequest().getContextPath();
            if (contexto != null && !contexto.isEmpty() && uri.startsWith(contexto)) {
                return uri.substring(contexto.length());
            }
            return uri;
        }
        return "";
    }

    private static void publicarAvisos(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        pasar(model, session, "aviso");
        pasar(model, session, "error");
    }

    private static void pasar(Model model, HttpSession session, String nombre) {
        Object valor = session.getAttribute(nombre);
        if (valor != null) {
            model.addAttribute(nombre, valor);
            session.removeAttribute(nombre);
        }
    }
}
