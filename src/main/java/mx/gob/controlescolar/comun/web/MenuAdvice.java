package mx.gob.controlescolar.comun.web;

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
    public void menu(Model model) {
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
        model.addAttribute("autenticado", usuario != null);
    }
}
