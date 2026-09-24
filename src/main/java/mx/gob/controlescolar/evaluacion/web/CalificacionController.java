package mx.gob.controlescolar.evaluacion.web;

import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.academico.aplicacion.PlanService;
import mx.gob.controlescolar.academico.aplicacion.ProgramaService;
import mx.gob.controlescolar.academico.dominio.Programa;
import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.evaluacion.aplicacion.CalificacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CalificacionController {

    private final CalificacionService calificaciones;
    private final ProgramaService programas;
    private final PlanService planes;
    private final PerfilService perfiles;
    private final SesionActual sesion;

    @GetMapping("/calificaciones")
    public String formulario(Model model) {
        Long escuela = sesion.institucionId();
        perfiles.exigir(sesion.usuario().getId(), "EVALUACION_CONSULTAR");
        List<Programa> deLaEscuela = programas.deLaEscuela(escuela);
        model.addAttribute("programas", deLaEscuela);
        model.addAttribute("momentos", deLaEscuela.stream()
                .flatMap(programa -> planes.momentosDe(programa.getPlanVersion().getId()).stream())
                .toList());
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

    @PostMapping("/calificaciones/faltas")
    public String faltas(@RequestParam Long alumnoId, @RequestParam String asignatura, @RequestParam int periodoOrden,
                         @RequestParam Long momentoId, @RequestParam int faltas) {
        perfiles.exigir(sesion.usuario().getId(), "EVALUACION_CAPTURAR");
        calificaciones.registrarFaltas(sesion.institucionId(), alumnoId, asignatura, periodoOrden, momentoId, faltas);
        return "redirect:/calificaciones";
    }
}
