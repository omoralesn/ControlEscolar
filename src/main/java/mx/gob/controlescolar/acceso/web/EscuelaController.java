package mx.gob.controlescolar.acceso.web;

import java.util.EnumSet;
import java.util.Set;

import mx.gob.controlescolar.acceso.aplicacion.CentroTrabajoService;
import mx.gob.controlescolar.acceso.aplicacion.EscuelaService;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

/** Rutas legacy /escuelas (ROLE_SUPER); el flujo principal está en /acceso/escuelas. */
@Controller
@RequiredArgsConstructor
public class EscuelaController {

    private final EscuelaService escuelas;
    private final CentroTrabajoService centros;

    @GetMapping("/escuelas")
    public String listar(Model model) {
        model.addAttribute("escuelas", escuelas.listar());
        model.addAttribute("modulos", Modulo.values());
        model.addAttribute("centros", escuelas.listar().stream()
                .flatMap(escuela -> centros.deLaEscuela(escuela.getId()).stream())
                .distinct()
                .toList());
        return "acceso/escuelas";
    }

    @PostMapping("/escuelas")
    public String alta(@RequestParam String nombre,
                       @RequestParam String claveCct,
                       @RequestParam(required = false) String sostenimiento,
                       @RequestParam(required = false) Set<Modulo> habilitados) {
        Set<Modulo> activos = habilitados == null ? EnumSet.noneOf(Modulo.class) : EnumSet.copyOf(habilitados);
        escuelas.altaPorCct(nombre, claveCct, sostenimiento, activos);
        return "redirect:/escuelas";
    }

    @PostMapping("/escuelas/{id}/suspender")
    public String suspender(@PathVariable Long id) {
        escuelas.suspender(id);
        return "redirect:/escuelas";
    }

    @PostMapping("/escuelas/{id}/activar")
    public String activar(@PathVariable Long id) {
        escuelas.activar(id);
        return "redirect:/escuelas";
    }

    @PostMapping("/escuelas/modulos")
    public String modulos(@RequestParam Long institucionId, @RequestParam(required = false) Set<Modulo> habilitados) {
        escuelas.definirModulos(institucionId, habilitados == null ? EnumSet.noneOf(Modulo.class) : EnumSet.copyOf(habilitados));
        return "redirect:/escuelas";
    }
}
