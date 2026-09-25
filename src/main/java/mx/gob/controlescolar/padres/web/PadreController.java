package mx.gob.controlescolar.padres.web;

import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.padres.aplicacion.PadreService;
import mx.gob.controlescolar.personas.aplicacion.AccesoTutorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PadreController {

    private final PadreService padres;
    private final AccesoTutorService accesos;
    private final SesionActual sesion;

    @GetMapping("/padres")
    public String consulta(Model model) {
        var usuario = sesion.usuario();
        Long escuela = usuario.getInstitucion().getId();
        model.addAttribute("consulta", padres.consultar(escuela, usuario.getId()));
        model.addAttribute("matricula", usuario.getUsername());
        return "padres/consulta";
    }

    @PostMapping("/padres/clave")
    public String clave(@RequestParam String claveTutor) {
        accesos.cambiarPropia(sesion.usuario(), claveTutor);
        return "redirect:/padres";
    }
}
