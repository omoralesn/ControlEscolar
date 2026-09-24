package mx.gob.controlescolar.personas.web;

import lombok.Generated;
import mx.gob.controlescolar.academico.aplicacion.PlanService;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.inscripcion.aplicacion.GrupoService;
import mx.gob.controlescolar.personas.aplicacion.AlumnoService;
import mx.gob.controlescolar.personas.aplicacion.TrayectoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AlumnoController {
    private final AlumnoService alumnos;
    private final TrayectoriaService trayectoria;
    private final GrupoService grupos;
    private final PlanService planes;
    private final PerfilService perfiles;
    private final SesionActual sesion;

    @GetMapping(value={"/alumnos"})
    public String listar(@RequestParam(required=false) Long grupoId, Model model) {
        Long escuela = this.sesion.institucionId();
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CONSULTAR");
        model.addAttribute("grupos", this.grupos.consultar(escuela));
        model.addAttribute("versiones", this.planes.vigentes());
        model.addAttribute("inscripciones", this.alumnos.activas(escuela));
        model.addAttribute("generaciones", this.trayectoria.generaciones(escuela));
        if (grupoId != null) {
            model.addAttribute("porGrupo", this.alumnos.porGrupo(escuela, grupoId));
        }
        return "personas/alumnos";
    }

    @PostMapping(value={"/alumnos"})
    public String registrar(@RequestParam Long grupoId, @RequestParam String curp, @RequestParam String nombre, @RequestParam String apellidoPaterno, @RequestParam(required=false) String apellidoMaterno) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        this.alumnos.registrar(this.sesion.institucionId(), grupoId, curp, nombre, apellidoPaterno, apellidoMaterno);
        return "redirect:/alumnos";
    }

    @PostMapping(value={"/alumnos/nombre"})
    public String nombre(@RequestParam Long alumnoId, @RequestParam String nombre) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        this.alumnos.modificar(this.sesion.institucionId(), alumnoId, nombre);
        return "redirect:/alumnos";
    }

    @PostMapping(value={"/alumnos/baja"})
    public String baja(@RequestParam Long alumnoId) {
        return this.mover(alumnoId, "baja");
    }

    @PostMapping(value={"/alumnos/reinscribir"})
    public String reinscribir(@RequestParam Long alumnoId) {
        return this.mover(alumnoId, "reinscribir");
    }

    @PostMapping(value={"/alumnos/trasladar"})
    public String trasladar(@RequestParam Long alumnoId) {
        return this.mover(alumnoId, "trasladar");
    }

    @PostMapping(value={"/alumnos/repetidor"})
    public String repetidor(@RequestParam Long alumnoId) {
        return this.mover(alumnoId, "repetidor");
    }

    @PostMapping(value={"/alumnos/grupo"})
    public String grupo(@RequestParam Long alumnoId, @RequestParam Long grupoId) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        this.alumnos.cambiarGrupo(this.sesion.institucionId(), alumnoId, grupoId);
        return "redirect:/alumnos";
    }

    @PostMapping(value={"/alumnos/plan"})
    public String plan(@RequestParam Long alumnoId, @RequestParam Long planVersionId) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        this.alumnos.cambiarPlan(this.sesion.institucionId(), alumnoId, planVersionId);
        return "redirect:/alumnos";
    }

    @PostMapping(value={"/alumnos/promover"})
    public String promover(@RequestParam Long alumnoId, @RequestParam Long grupoId) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        this.trayectoria.promover(this.sesion.institucionId(), alumnoId, grupoId);
        return "redirect:/alumnos";
    }

    @PostMapping(value={"/alumnos/generacion"})
    public String generacion(@RequestParam String nombre, @RequestParam int anioInicio) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        this.trayectoria.crearGeneracion(this.sesion.institucionId(), nombre, anioInicio);
        return "redirect:/alumnos";
    }

    @PostMapping(value={"/alumnos/generacion/asignar"})
    public String asignarGeneracion(@RequestParam Long alumnoId, @RequestParam(required=false) Long grupoId, @RequestParam Long generacionId) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        this.trayectoria.asignarGeneracion(this.sesion.institucionId(), alumnoId, grupoId, generacionId);
        return "redirect:/alumnos";
    }

    @PostMapping(value={"/alumnos/egresar"})
    public String egresar(@RequestParam Long alumnoId) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        this.trayectoria.egresar(this.sesion.institucionId(), alumnoId);
        return "redirect:/alumnos";
    }

    @PostMapping(value={"/alumnos/umbral"})
    public String umbral(@RequestParam Long planVersionId, @RequestParam Integer umbral) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "PLANES_CONFIGURAR");
        this.planes.definirUmbralCreditos(planVersionId, umbral);
        return "redirect:/alumnos";
    }

    private String mover(Long alumnoId, String tipo) {
        this.perfiles.exigir(this.sesion.usuario().getId(), "ALUMNOS_CAPTURAR");
        Long escuela = this.sesion.institucionId();
        switch (tipo) {
            case "baja": {
                this.alumnos.baja(escuela, alumnoId);
                break;
            }
            case "reinscribir": {
                this.alumnos.reinscribir(escuela, alumnoId);
                break;
            }
            case "trasladar": {
                this.alumnos.trasladar(escuela, alumnoId);
                break;
            }
            default: {
                this.alumnos.repetidor(escuela, alumnoId);
            }
        }
        return "redirect:/alumnos";
    }

    @Generated
    public AlumnoController(AlumnoService alumnos, TrayectoriaService trayectoria, GrupoService grupos, PlanService planes, PerfilService perfiles, SesionActual sesion) {
        this.alumnos = alumnos;
        this.trayectoria = trayectoria;
        this.grupos = grupos;
        this.planes = planes;
        this.perfiles = perfiles;
        this.sesion = sesion;
    }
}
