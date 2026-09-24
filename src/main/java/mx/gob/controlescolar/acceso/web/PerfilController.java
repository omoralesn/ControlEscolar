package mx.gob.controlescolar.acceso.web;

import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.comun.web.SesionActual;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfiles;
    private final SesionActual sesion;

    @GetMapping("/perfiles")
    public String listar(Model model) {
        Long escuela = sesion.institucionId();
        model.addAttribute("perfiles", perfiles.deLaEscuela(escuela));
        model.addAttribute("disponibles", perfiles.disponibles(escuela));
        return "acceso/perfiles";
    }

    @PostMapping("/perfiles")
    public String crear(@RequestParam String nombre, @RequestParam(required = false) Set<String> codigos) {
        perfiles.crear(sesion.institucionId(), nombre, codigos == null ? Set.of() : codigos);
        return "redirect:/perfiles";
    }

    @PostMapping("/perfiles/usuarios")
    public String usuario(@RequestParam String login, @RequestParam String clave, @RequestParam String nombre,
                          @RequestParam Long perfilId) {
        perfiles.crearUsuario(sesion.institucionId(), login, clave, nombre, perfilId);
        return "redirect:/perfiles";
    }
}
