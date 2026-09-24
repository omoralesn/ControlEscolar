package mx.gob.controlescolar.academico.web;

import java.time.LocalDate;
import lombok.Generated;
import mx.gob.controlescolar.academico.aplicacion.CalendarioService;
import mx.gob.controlescolar.academico.aplicacion.PlanService;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.comun.web.SesionActual;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarioController {
    private final CalendarioService calendarios;
    private final PlanService planes;
    private final PerfilService perfiles;
    private final SesionActual sesion;

    @GetMapping(value={"/calendarios"})
    public String listar(Model model) {
        if (this.sesion.usuario() != null && this.sesion.usuario().esSuper()) {
            throw new NegocioException("El calendario oficial lo publica el administrador de plataforma");
        }
        model.addAttribute("ciclos", this.calendarios.ciclos());
        model.addAttribute("oficiales", this.calendarios.oficiales());
        if (this.sesion.usuario() != null && this.sesion.usuario().esPlataforma()) {
            return "academico/calendarios";
        }
        Long escuela = this.sesion.institucionId();
        this.perfiles.exigir(this.sesion.usuario().getId(), "PLANES_CONSULTAR");
        model.addAttribute("propios", this.calendarios.deLaEscuela(escuela));
        model.addAttribute("versiones", this.planes.vigentes());
        return "academico/calendarios";
    }

    @PostMapping(value={"/calendarios"})
    public String crear(@RequestParam Long cicloId, @RequestParam String nivel, @RequestParam(required=false) String tipoPeriodo, @RequestParam(required=false) Integer diasEfectivos) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "PLANES_CONFIGURAR");
        this.calendarios.crearDeEscuela(this.sesion.institucionId(), cicloId, nivel, tipoPeriodo, diasEfectivos);
        return "redirect:/calendarios";
    }

    @PostMapping(value={"/calendarios/eventos"})
    public String evento(@RequestParam Long calendarioId, @RequestParam String tipo, @RequestParam String nombre, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate inicio, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fin) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "PLANES_CONFIGURAR");
        this.calendarios.agregarEvento(calendarioId, tipo, nombre, inicio, fin);
        return "redirect:/calendarios";
    }

    @PostMapping(value={"/calendarios/periodos"})
    public String periodo(@RequestParam Long calendarioId, @RequestParam Long periodoPlanId, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate inicio, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fin) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "PLANES_CONFIGURAR");
        this.calendarios.abrirPeriodo(calendarioId, periodoPlanId, inicio, fin);
        return "redirect:/calendarios";
    }

    @PostMapping(value={"/calendarios/ventanas"})
    public String ventana(@RequestParam Long periodoCicloId, @RequestParam Long momentoId, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate capturaDesde, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate capturaHasta, @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate publicacionDesde) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "PLANES_CONFIGURAR");
        this.calendarios.configurarVentana(periodoCicloId, momentoId, capturaDesde, capturaHasta, publicacionDesde);
        return "redirect:/calendarios";
    }

    @PostMapping(value={"/calendarios/cerrar"})
    public String cerrar(@RequestParam Long periodoCicloId) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "PLANES_CERRAR");
        this.calendarios.cerrarPeriodo(periodoCicloId);
        return "redirect:/calendarios";
    }

    @Generated
    public CalendarioController(CalendarioService calendarios, PlanService planes, PerfilService perfiles, SesionActual sesion) {
        this.calendarios = calendarios;
        this.planes = planes;
        this.perfiles = perfiles;
        this.sesion = sesion;
    }
}
