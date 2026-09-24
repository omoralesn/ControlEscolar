package mx.gob.controlescolar.comun.web;

import mx.gob.controlescolar.comun.aplicacion.EstadoPlataformaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InicioController {

    private final EstadoPlataformaService estadoPlataformaService;

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("nombre", estadoPlataformaService.nombre());
        return "comun/inicio";
    }
}
