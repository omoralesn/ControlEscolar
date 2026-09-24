package mx.gob.controlescolar.acceso.web;

import java.util.EnumSet;
import java.util.Set;
import lombok.Generated;
import mx.gob.controlescolar.acceso.aplicacion.CentroTrabajoService;
import mx.gob.controlescolar.acceso.aplicacion.EscuelaService;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EscuelaController {
    private final EscuelaService escuelas;
    private final CentroTrabajoService centros;

    @GetMapping(value={"/escuelas"})
    public String listar(Model model) {
        model.addAttribute("escuelas", this.escuelas.listar());
        model.addAttribute("modulos", (Object)Modulo.values());
        model.addAttribute("centros", this.escuelas.listar().stream().flatMap(escuela -> this.centros.delPlantel(escuela.getId()).stream()).distinct().toList());
        return "acceso/escuelas";
    }

    @PostMapping(value={"/escuelas"})
    public String alta(@RequestParam String nombre, @RequestParam(required=false) String claveCct, @RequestParam(defaultValue="false") boolean particular, @RequestParam(required=false) String nivel, @RequestParam(required=false) String sostenimiento, @RequestParam(required=false) Set<Modulo> habilitados, @RequestParam String loginEscolar, @RequestParam String claveEscolar) {
        EnumSet<Modulo> modulos;
        EnumSet<Modulo> enumSet = modulos = habilitados == null ? EnumSet.noneOf(Modulo.class) : EnumSet.copyOf(habilitados);
        if (nivel != null && !nivel.isBlank()) {
            this.escuelas.altaNivel(nombre, claveCct, particular, nivel, sostenimiento, modulos, loginEscolar, claveEscolar);
        } else {
            this.escuelas.alta(nombre, claveCct, particular, modulos, loginEscolar, claveEscolar);
        }
        return "redirect:/escuelas";
    }

    @PostMapping(value={"/escuelas/{id}/suspender"})
    public String suspender(@PathVariable Long id) {
        this.escuelas.suspender(id);
        return "redirect:/escuelas";
    }

    @PostMapping(value={"/escuelas/{id}/activar"})
    public String activar(@PathVariable Long id) {
        this.escuelas.activar(id);
        return "redirect:/escuelas";
    }

    @PostMapping(value={"/escuelas/modulos"})
    public String modulos(@RequestParam Long institucionId, @RequestParam(required=false) Set<Modulo> habilitados) {
        this.escuelas.definirModulos(institucionId, habilitados == null ? EnumSet.noneOf(Modulo.class) : EnumSet.copyOf(habilitados));
        return "redirect:/escuelas";
    }

    @Generated
    public EscuelaController(EscuelaService escuelas, CentroTrabajoService centros) {
        this.escuelas = escuelas;
        this.centros = centros;
    }
}
