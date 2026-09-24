package mx.gob.controlescolar.padres.web;

import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.padres.aplicacion.PadreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PadreController {

    private final PadreService padres;
    private final SesionActual sesion;

    @GetMapping("/padres")
    public String consulta(Model model) {
        Long escuela = sesion.institucionId();
        model.addAttribute("consulta", padres.consultar(escuela, sesion.usuario().getId()));
        return "padres/consulta";
    }
}
