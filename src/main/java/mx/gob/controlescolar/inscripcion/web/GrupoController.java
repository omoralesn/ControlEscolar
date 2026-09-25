package mx.gob.controlescolar.inscripcion.web;

import mx.gob.controlescolar.acceso.aplicacion.ModuloGuardia;
import mx.gob.controlescolar.acceso.aplicacion.PerfilService;
import mx.gob.controlescolar.acceso.dominio.Modulo;
import mx.gob.controlescolar.academico.aplicacion.PlanService;
import mx.gob.controlescolar.academico.aplicacion.ProgramaService;
import mx.gob.controlescolar.academico.dominio.Programa;
import mx.gob.controlescolar.comun.web.SesionActual;
import mx.gob.controlescolar.inscripcion.aplicacion.GrupoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService grupos;
    private final ProgramaService programas;
    private final PlanService planes;
    private final PerfilService perfiles;
    private final ModuloGuardia modulos;
    private final SesionActual sesion;

    @GetMapping("/grupos")
    public String listar(Model model) {
        Long escuela = sesion.institucionId();
        var deLaEscuela = programas.deLaEscuela(escuela);
        var periodos = new ArrayList<mx.gob.controlescolar.academico.dominio.PeriodoPlan>();
        var materias = new ArrayList<mx.gob.controlescolar.academico.dominio.AsignaturaPlan>();
        for (Programa programa : deLaEscuela) {
            Long planId = programa.getPlanVersion().getId();
            periodos.addAll(planes.periodosDe(planId));
            materias.addAll(planes.asignaturasDe(planId));
        }
        model.addAttribute("grupos", grupos.consultar(escuela));
        model.addAttribute("programas", deLaEscuela);
        model.addAttribute("periodos", periodos);
        model.addAttribute("materias", materias);
        model.addAttribute("profesores", modulos.activo(escuela, Modulo.PLANTILLA)
                ? grupos.plantilla(escuela) : java.util.List.of());
        model.addAttribute("horarios", modulos.activo(escuela, Modulo.PLANTILLA)
                ? grupos.horarios(escuela) : java.util.List.of());
        return "inscripcion/grupos";
    }

    @PostMapping("/grupos")
    public String registrar(@RequestParam Long programaId, @RequestParam String nombre,
                            @RequestParam(required = false) Integer periodoOrden,
                            @RequestParam(required = false) Integer periodoNuevo,
                            @RequestParam(required = false) String edificio,
                            @RequestParam(required = false) String aula,
                            @RequestParam int capacidad) {
        perfiles.exigir(sesion.usuario().getId(), "INSCRIPCION_CAPTURAR");
        int orden = periodoNuevo != null ? periodoNuevo : periodoOrden == null ? 0 : periodoOrden;
        if (orden < 1) {
            throw new mx.gob.controlescolar.comun.aplicacion.NegocioException("Indique el periodo del grupo");
        }
        grupos.registrar(sesion.institucionId(), programaId, nombre, orden, edificio, aula, capacidad);
        return "redirect:/grupos";
    }

    @PostMapping("/profesores")
    public String profesor(@RequestParam String nombre) {
        perfiles.exigir(sesion.usuario().getId(), "PLANTILLA_CAPTURAR");
        grupos.registrarProfesor(sesion.institucionId(), nombre);
        return "redirect:/grupos";
    }

    @PostMapping("/horarios")
    public String horario(@RequestParam Long grupoId, @RequestParam Long profesorId,
                          @RequestParam(required = false) String asignatura,
                          @RequestParam(required = false) String asignaturaLibre,
                          @RequestParam String dia, @RequestParam String horaInicio, @RequestParam String horaFin) {
        perfiles.exigir(sesion.usuario().getId(), "PLANTILLA_CAPTURAR");
        String clave = asignaturaLibre != null && !asignaturaLibre.isBlank() ? asignaturaLibre.trim() : asignatura;
        if (clave == null || clave.isBlank()) {
            throw new mx.gob.controlescolar.comun.aplicacion.NegocioException("Elija una asignatura del catálogo o regístrela");
        }
        grupos.asignar(sesion.institucionId(), grupoId, profesorId, clave, dia, horaInicio, horaFin);
        return "redirect:/grupos";
    }
}
