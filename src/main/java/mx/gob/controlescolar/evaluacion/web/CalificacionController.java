package mx.gob.controlescolar.evaluacion.web;

import jakarta.servlet.http.HttpServletRequest;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.academico.aplicacion.PlanService;
import mx.gob.controlescolar.academico.dominio.AsignaturaPlan;
import mx.gob.controlescolar.academico.dominio.MomentoEvaluacion;
import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.evaluacion.aplicacion.CalificacionService;
import mx.gob.controlescolar.evaluacion.dominio.Calificacion;
import mx.gob.controlescolar.inscripcion.aplicacion.GrupoService;
import mx.gob.controlescolar.inscripcion.dominio.Grupo;
import mx.gob.controlescolar.personas.aplicacion.AlumnoService;
import mx.gob.controlescolar.personas.dominio.Inscripcion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CalificacionController {

    private final CalificacionService calificaciones;
    private final PlanService planes;
    private final GrupoService grupos;
    private final AlumnoService alumnos;
    private final PerfilService perfiles;
    private final SesionActual sesion;

    @GetMapping("/calificaciones")
    public String formulario(@RequestParam(required = false) Long grupoId,
                             @RequestParam(required = false) Long momentoId, Model model) {
        Long escuela = sesion.institucionId();
        perfiles.exigir(sesion.usuario().getId(), "EVALUACION_CONSULTAR");
        List<Grupo> deLaEscuela = grupos.consultar(escuela);
        model.addAttribute("grupos", deLaEscuela);
        model.addAttribute("grupoId", grupoId);
        model.addAttribute("momentoId", momentoId);
        if (grupoId == null) {
            model.addAttribute("momentos", List.of());
            return "evaluacion/calificaciones";
        }
        Grupo grupo = deLaEscuela.stream().filter(item -> item.getId().equals(grupoId)).findFirst().orElseThrow();
        Long planId = grupo.getPrograma().getPlanVersion().getId();
        List<MomentoEvaluacion> momentos = planes.momentosDe(planId);
        List<AsignaturaPlan> materias = planes.asignaturasDe(planId);
        List<Inscripcion> filas = alumnos.activas(escuela).stream()
                .filter(inscripcion -> inscripcion.getGrupo().getId().equals(grupoId))
                .toList();
        Map<String, Calificacion> capturado = new LinkedHashMap<>();
        if (momentoId != null) {
            for (Inscripcion inscripcion : filas) {
                for (Calificacion calificacion : calificaciones.delAlumno(inscripcion.getAlumno().getId(), planId)) {
                    if (calificacion.getMomentoId().equals(momentoId) && calificacion.getPeriodoOrden() == grupo.getPeriodoOrden()) {
                        capturado.put(inscripcion.getAlumno().getId() + "_" + calificacion.getAsignaturaClave(), calificacion);
                    }
                }
            }
        }
        model.addAttribute("grupo", grupo);
        model.addAttribute("momentos", momentos);
        model.addAttribute("materias", materias);
        model.addAttribute("filas", filas);
        model.addAttribute("capturado", capturado);
        return "evaluacion/calificaciones";
    }

    @PostMapping("/calificaciones")
    public String capturar(@RequestParam Long alumnoId, @RequestParam String asignatura, @RequestParam int periodoOrden,
                           @RequestParam Long momentoId, @RequestParam(required = false) BigDecimal valor,
                           @RequestParam(required = false) String observacion, @RequestParam(required = false) String acta,
                           @RequestParam(defaultValue = "false") boolean complementaria) {
        perfiles.exigir(sesion.usuario().getId(), "EVALUACION_CAPTURAR");
        calificaciones.capturar(sesion.institucionId(), alumnoId, asignatura, periodoOrden, momentoId,
                valor, observacion, acta, complementaria);
        return "redirect:/calificaciones";
    }

    @PostMapping("/calificaciones/matriz")
    public String matriz(@RequestParam Long grupoId, @RequestParam Long momentoId, HttpServletRequest solicitud) {
        perfiles.exigir(sesion.usuario().getId(), "EVALUACION_CAPTURAR");
        Grupo grupo = grupos.consultar(sesion.institucionId()).stream()
                .filter(item -> item.getId().equals(grupoId)).findFirst().orElseThrow();
        Map<String, String> celdas = new LinkedHashMap<>();
        solicitud.getParameterMap().forEach((nombre, valores) -> {
            if ((nombre.startsWith("v_") || nombre.startsWith("f_")) && valores.length > 0) {
                celdas.put(nombre, valores[0]);
            }
        });
        calificaciones.capturarMatriz(sesion.institucionId(), grupo.getPeriodoOrden(), momentoId, celdas);
        return "redirect:/calificaciones?grupoId=" + grupoId + "&momentoId=" + momentoId;
    }

    @PostMapping("/calificaciones/faltas")
    public String faltas(@RequestParam Long alumnoId, @RequestParam String asignatura, @RequestParam int periodoOrden,
                         @RequestParam Long momentoId, @RequestParam int faltas) {
        perfiles.exigir(sesion.usuario().getId(), "EVALUACION_CAPTURAR");
        calificaciones.registrarFaltas(sesion.institucionId(), alumnoId, asignatura, periodoOrden, momentoId, faltas);
        return "redirect:/calificaciones";
    }
}
