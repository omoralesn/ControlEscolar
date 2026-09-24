package mx.gob.controlescolar.acceso.web;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;
import lombok.Generated;
import mx.gob.controlescolar.acceso.aplicacion.AccesoService;
import mx.gob.controlescolar.acceso.aplicacion.CentroTrabajoService;
import mx.gob.controlescolar.acceso.aplicacion.EscuelaService;
import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.acceso.dominio.CatalogoPaginas;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccesoController {
    private final EscuelaService escuelas;
    private final CentroTrabajoService centros;
    private final AccesoService acceso;
    private final PerfilService perfiles;
    private final ModuloGuardia modulos;

    @GetMapping(value={"/acceso"})
    public String listar(@RequestParam(required=false) Long escuelaId, Model model) {
        model.addAttribute("escuelas", this.escuelas.listar());
        model.addAttribute("planteles", this.acceso.planteles());
        model.addAttribute("modulos", (Object)Modulo.values());
        model.addAttribute("paginas", CatalogoPaginas.TODAS);
        model.addAttribute("centros", this.escuelas.listar().stream().flatMap(escuela -> this.centros.deLaEscuela(escuela.getId()).stream()).toList());
        if (escuelaId != null) {
            model.addAttribute("escuelaId", (Object)escuelaId);
            model.addAttribute("usuarios", this.acceso.deLaEscuela(escuelaId));
            model.addAttribute("perfiles", this.perfiles.deLaEscuela(escuelaId));
            model.addAttribute("disponibles", this.perfiles.disponibles(escuelaId));
            model.addAttribute("modulosActivos", this.modulos.activos(escuelaId));
        }
        return "acceso/acceso";
    }

    @PostMapping(value={"/acceso/escuelas"})
    public String alta(@RequestParam String nombre, @RequestParam(required=false) String claveCct, @RequestParam(defaultValue="false") boolean particular, @RequestParam(required=false) String nivel, @RequestParam(required=false) String sostenimiento, @RequestParam(required=false) Set<Modulo> habilitados, @RequestParam String loginEscolar, @RequestParam String claveEscolar) {
        EnumSet<Modulo> activos;
        EnumSet<Modulo> enumSet = activos = habilitados == null ? EnumSet.noneOf(Modulo.class) : EnumSet.copyOf(habilitados);
        if (nivel != null && !nivel.isBlank()) {
            this.escuelas.altaNivel(nombre, claveCct, particular, nivel, sostenimiento, activos, loginEscolar, claveEscolar);
        } else {
            this.escuelas.alta(nombre, claveCct, particular, activos, loginEscolar, claveEscolar);
        }
        return "redirect:/acceso";
    }

    @PostMapping(value={"/acceso/cct"})
    public String cct(@RequestParam Long institucionId, @RequestParam String clave, @RequestParam(required=false) String sostenimiento, @RequestParam String nombre) {
        this.centros.registrar(institucionId, clave, sostenimiento, nombre);
        return "redirect:/acceso?escuelaId=" + institucionId;
    }

    @PostMapping(value={"/acceso/modulos"})
    public String modulos(@RequestParam Long institucionId, @RequestParam(required=false) Set<Modulo> habilitados) {
        this.escuelas.definirModulos(institucionId, habilitados == null ? EnumSet.noneOf(Modulo.class) : EnumSet.copyOf(habilitados));
        return "redirect:/acceso?escuelaId=" + institucionId;
    }

    @PostMapping(value={"/acceso/escuelas/{id}/suspender"})
    public String suspenderEscuela(@PathVariable Long id) {
        this.escuelas.suspender(id);
        return "redirect:/acceso?escuelaId=" + id;
    }

    @PostMapping(value={"/acceso/escuelas/{id}/activar"})
    public String activarEscuela(@PathVariable Long id) {
        this.escuelas.activar(id);
        return "redirect:/acceso?escuelaId=" + id;
    }

    @PostMapping(value={"/acceso/planteles/{id}/suspender"})
    public String suspenderPlantel(@PathVariable Long id) {
        this.acceso.suspenderPlantel(id);
        return "redirect:/acceso";
    }

    @PostMapping(value={"/acceso/planteles/{id}/activar"})
    public String activarPlantel(@PathVariable Long id) {
        this.acceso.activarPlantel(id);
        return "redirect:/acceso";
    }

    @PostMapping(value={"/acceso/usuarios/vigencia"})
    public String vigencia(@RequestParam Long usuarioId, @RequestParam Long escuelaId, @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate vigenteDesde, @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate vigenteHasta) {
        this.acceso.definirVigencia(usuarioId, vigenteDesde, vigenteHasta);
        return "redirect:/acceso?escuelaId=" + escuelaId;
    }

    @PostMapping(value={"/acceso/usuarios/{id}/suspender"})
    public String suspenderUsuario(@PathVariable Long id, @RequestParam Long escuelaId) {
        this.acceso.suspenderUsuario(id);
        return "redirect:/acceso?escuelaId=" + escuelaId;
    }

    @PostMapping(value={"/acceso/usuarios/{id}/activar"})
    public String activarUsuario(@PathVariable Long id, @RequestParam Long escuelaId) {
        this.acceso.activarUsuario(id);
        return "redirect:/acceso?escuelaId=" + escuelaId;
    }

    @PostMapping(value={"/acceso/perfiles"})
    public String perfil(@RequestParam Long escuelaId, @RequestParam String nombre, @RequestParam(required=false) Set<String> codigos) {
        this.perfiles.crear(escuelaId, nombre, codigos == null ? Set.of() : codigos);
        return "redirect:/acceso?escuelaId=" + escuelaId;
    }

    @Generated
    public AccesoController(EscuelaService escuelas, CentroTrabajoService centros, AccesoService acceso, PerfilService perfiles, ModuloGuardia modulos) {
        this.escuelas = escuelas;
        this.centros = centros;
        this.acceso = acceso;
        this.perfiles = perfiles;
        this.modulos = modulos;
    }
}
