package mx.gob.controlescolar.academico.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Generated;
import mx.gob.controlescolar.academico.aplicacion.PlanService;
import mx.gob.controlescolar.academico.aplicacion.ProgramaService;
import mx.gob.controlescolar.academico.dominio.AsignaturaPlan;
import mx.gob.controlescolar.academico.dominio.CatalogoPlanesSep;
import mx.gob.controlescolar.academico.dominio.PlanVersion;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.comun.aplicacion.NegocioException;
import mx.gob.controlescolar.comun.web.SesionActual;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PlanController {
    private final PlanService planes;
    private final ProgramaService programas;
    private final PerfilService perfiles;
    private final SesionActual sesion;

    @GetMapping(value={"/planes"})
    public String listar(Model model) {
        if (this.sesion.usuario() != null && this.sesion.usuario().esSuper()) {
            throw new NegocioException("Los planes generales los publica el administrador de plataforma");
        }
        List<PlanVersion> versiones = this.planes.vigentes();
        model.addAttribute("versiones", versiones);
        model.addAttribute("asignaturasPorPlan", this.asignaturasDe(versiones));
        model.addAttribute("versionesSinListaNacional", versiones.stream().filter(version -> !CatalogoPlanesSep.tieneListaNacional(version.getPlanRector().getNivel(), version.getPlanRector().getNombre())).toList());
        if (this.sesion.usuario() != null && this.sesion.usuario().esPlataforma()) {
            model.addAttribute("rectores", this.planes.listarRectores());
            return "academico/planes";
        }
        Long escuela = this.sesion.institucionId();
        this.perfiles.exigir(this.sesion.usuario().getId(), "PLANES_CONSULTAR");
        model.addAttribute("programas", this.programas.deLaEscuela(escuela));
        return "academico/planes";
    }

    @PostMapping(value={"/planes/asignaturas"})
    public String asignatura(@RequestParam Long planVersionId, @RequestParam String clave, @RequestParam String nombre, @RequestParam(required=false) String campo) {
        if (this.sesion.usuario() == null || !this.sesion.usuario().esPlataforma()) {
            throw new NegocioException("Las asignaturas del plan general las registra la plataforma");
        }
        PlanVersion version = this.planes.obtener(planVersionId);
        if (CatalogoPlanesSep.tieneListaNacional(version.getPlanRector().getNivel(), version.getPlanRector().getNombre())) {
            throw new NegocioException("Este plan ya trae las asignaturas del plan de la SEP");
        }
        this.planes.agregarAsignatura(planVersionId, clave, nombre, null, null, false, campo == null || campo.isBlank() ? null : campo, null);
        return "redirect:/planes";
    }

    @PostMapping(value={"/planes/adoptar"})
    public String adoptar(@RequestParam Long planVersionId, @RequestParam String nombre) {
        Long escuela = this.sesion.institucionId();
        this.perfiles.exigir(this.sesion.usuario().getId(), "PLANES_CONFIGURAR");
        this.programas.adoptar(escuela, planVersionId, nombre);
        return "redirect:/planes";
    }

    @PostMapping(value={"/planes/anexar"})
    public String anexar(@RequestParam Long programaId, @RequestParam String clave, @RequestParam String nombre) {
        Long escuela = this.sesion.institucionId();
        this.perfiles.exigir(this.sesion.usuario().getId(), "PLANES_CONFIGURAR");
        this.programas.anexar(escuela, programaId, clave, nombre);
        return "redirect:/planes";
    }

    @PostMapping(value={"/planes/cerrar"})
    public String cerrar(@RequestParam Long planVersionId) {
        Long escuela = this.sesion.institucionId();
        this.perfiles.exigir(this.sesion.usuario().getId(), "PLANES_CERRAR");
        this.planes.cerrarVersion(escuela, planVersionId);
        return "redirect:/planes";
    }

    @PostMapping(value={"/planes/esquema"})
    public String esquema(@RequestParam Long planVersionId, @RequestParam BigDecimal aprobatoria, @RequestParam int ordinarios, @RequestParam(defaultValue="false") boolean extraordinarios, @RequestParam(defaultValue="false") boolean segundoCurso) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "PLANES_CONFIGURAR");
        this.planes.configurarEsquema(planVersionId, aprobatoria, ordinarios, extraordinarios, segundoCurso);
        return "redirect:/planes";
    }

    private Map<Long, List<AsignaturaPlan>> asignaturasDe(List<PlanVersion> versiones) {
        return versiones.stream().collect(Collectors.toMap(PlanVersion::getId, version -> this.planes.asignaturasDe(version.getId())));
    }

    @Generated
    public PlanController(PlanService planes, ProgramaService programas, PerfilService perfiles, SesionActual sesion) {
        this.planes = planes;
        this.programas = programas;
        this.perfiles = perfiles;
        this.sesion = sesion;
    }
}
