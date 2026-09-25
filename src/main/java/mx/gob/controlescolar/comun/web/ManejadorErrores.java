package mx.gob.controlescolar.comun.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;

@ControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(NegocioException.class)
    public Object negocio(NegocioException error, HttpServletRequest request) {
        String vuelta = vueltaSegura(request);
        if (vuelta == null) {
            request.setAttribute("mensaje", error.getMessage());
            return "comun/error";
        }
        request.getSession().setAttribute("error", error.getMessage());
        return new RedirectView(vuelta, true);
    }

    @ExceptionHandler(Exception.class)
    public String error(HttpServletRequest request) {
        request.setAttribute("mensaje", "No se pudo completar la operación.");
        return "comun/error";
    }

    private static String vueltaSegura(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return null;
        }
        try {
            URI uri = URI.create(referer);
            if (uri.getHost() != null && !uri.getHost().equalsIgnoreCase(request.getServerName())) {
                return null;
            }
            String camino = uri.getRawPath() == null ? "" : uri.getRawPath();
            String contexto = request.getContextPath();
            if (contexto != null && !contexto.isEmpty() && camino.startsWith(contexto)) {
                camino = camino.substring(contexto.length());
            }
            if (camino.isBlank() || !camino.startsWith("/")) {
                return null;
            }
            return uri.getRawQuery() == null ? camino : camino + "?" + uri.getRawQuery();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
