package mx.gob.controlescolar.comun.web;

import mx.gob.controlescolar.comun.web.SesionActual;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PanelController {

    private final SesionActual sesion;

    @GetMapping("/panel")
    public String panel() {
        if (sesion.usuario() != null && sesion.usuario().esTutor()) {
            return "redirect:/padres";
        }
        return "comun/panel";
    }
}
