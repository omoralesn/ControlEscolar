package mx.gob.controlescolar.acceso.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EntradaController {

    @GetMapping("/entrar")
    public String entrar(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("entrada") != null) {
            model.addAttribute("entrada", session.getAttribute("entrada"));
            session.removeAttribute("entrada");
        }
        return "acceso/entrar";
    }
}
